package com.example.donemate.data.repository.offline_first

import android.util.Log
import androidx.paging.PagingSource
import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.QuotesLocalDataSource
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.model.Quote
import com.example.donemate.data.model.asEntity
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.remote.LikedQuoteRecordsRemoteDataSource
import com.example.donemate.data.remote.QuotesApi
import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord
import com.example.donemate.data.remote.model.RemoteQuotesList
import com.example.donemate.data.repository.remote.AccountRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "OfflineFirstQuotesRepository"

class OfflineFirstQuotesRepository
@Inject constructor(
    private val quotesApi: QuotesApi,
    private val quotesLocalDataSource: QuotesLocalDataSource,
    private val accountRepository: AccountRepository,
    private val likedQuoteRecordsRemoteDataSource: LikedQuoteRecordsRemoteDataSource
) : QuotesRepository {
    override suspend fun getRemoteQuotesList(page: Int): Result<RemoteQuotesList> =
        safeCall("getRemoteQuotesList", TAG) {
            quotesApi.getQuotes(page)
        }

    // I didn't find a way to make error handling for this method
    override fun getAllQuotesPagingSource(): PagingSource<Int, QuoteEntity> {
        return quotesLocalDataSource.pagingSource()
    }

    override suspend fun clearAll() =
        safeCall("clearAll", TAG) {
            quotesLocalDataSource.clearAll()
        }

    override suspend fun upsertAll(quotes: List<QuoteEntity>) =
        safeCall("upsertAll", TAG) {
            quotesLocalDataSource.upsertAll(quotes)
        }

    override suspend fun upsert(quote: QuoteEntity) {
        safeCall("upsert", TAG) {
            quotesLocalDataSource.upsert(quote)
        }
    }


    override suspend fun markAsLiked(id: String) {
        safeCall("markAsLiked", TAG) {
            quotesLocalDataSource.markAsLikedAndNeedsSync(id, System.currentTimeMillis())
        }
    }

    override suspend fun markAsUnliked(id: String) {
        safeCall("markAsUnliked", TAG) {
            quotesLocalDataSource.markAsUnlikedAndNeedsSync(id, System.currentTimeMillis())
        }
    }

    override val likedQuotes: Flow<Result<List<Quote>>> =
        quotesLocalDataSource.getLikedQuotes()
            .map { entities ->
                val result: Result<List<Quote>> =
                    Result.Success(entities.map { it.asExternalModel() })
                result
            }
            .catch {
                emit(Result.Error("Something went wrong."))
            }

    /** Syncing quotes using last-write wins and soft deletes */
    override suspend fun sync(): Result<Unit> {
        return safeCall("sync", TAG) {
            val likedQuotes = quotesLocalDataSource.getLikedQuotes().first()
            val quotesNeedingSync = quotesLocalDataSource.getQuotesNeedingSync()
            val remoteLikedQuoteRecords =
                likedQuoteRecordsRemoteDataSource.getLikedQuoteRecords(accountRepository.getUserId()!!)

            // Remote → Local

            remoteLikedQuoteRecords.forEach { remoteLikedQuoteRecord ->
                val localQuote = quotesLocalDataSource.getAllQuotes()
                    .firstOrNull { localQuote ->
                        remoteLikedQuoteRecord.quoteId == localQuote.id
                    }

                if (localQuote == null) {
                    quotesLocalDataSource.upsert(
                        quotesApi.getQuoteById(remoteLikedQuoteRecord.quoteId).asEntity()
                    )
                    quotesLocalDataSource.markAsLiked(
                        remoteLikedQuoteRecord.quoteId,
                        remoteLikedQuoteRecord.updatedAt
                    )
                } else if (localQuote.updatedAt == null || localQuote.updatedAt < remoteLikedQuoteRecord.updatedAt) {
                    quotesLocalDataSource.markAsLiked(
                        localQuote.id,
                        remoteLikedQuoteRecord.updatedAt
                    )
                }
            }

            // Local → Remote
            Log.d("sync", "quotesNeedingSync: $quotesNeedingSync")
            quotesNeedingSync.forEach { quoteNeedingSync ->
                val remoteLikedQuoteRecord =
                    remoteLikedQuoteRecords.firstOrNull { remoteLikedQuoteRecord ->
                        remoteLikedQuoteRecord.quoteId == quoteNeedingSync.id
                    }

                if (remoteLikedQuoteRecord == null && quoteNeedingSync.isLiked) {
                    likedQuoteRecordsRemoteDataSource.save(
                        RemoteLikedQuoteRecord(
                            quoteId = quoteNeedingSync.id,
                            updatedAt = quoteNeedingSync.updatedAt!!,
                            userId = accountRepository.getUserId()!!
                        )
                    )
                } else if (remoteLikedQuoteRecord != null && remoteLikedQuoteRecord.updatedAt < quoteNeedingSync.updatedAt!!) {
                    if (!quoteNeedingSync.isLiked) {
                        likedQuoteRecordsRemoteDataSource.delete(
                            quoteNeedingSync.id,
                            accountRepository.getUserId()!!
                        )
                    }
                }
                quotesLocalDataSource.markAsNotNeedsSync(quoteNeedingSync.id)
            }

            likedQuotes.forEach { quote ->
                val remote = remoteLikedQuoteRecords.find { quote.id == it.quoteId }
                if (remote == null && !quote.needsSync) {
                    quotesLocalDataSource.markAsUnliked(quote.id, System.currentTimeMillis())
                }
            }
        }
    }
}
