package com.example.donemate.data.repository.offline_first

import androidx.paging.PagingSource
import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.QuotesLocalDataSource
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.model.Quote
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.remote.LikedQuoteRecordsRemoteDataSource
import com.example.donemate.data.remote.QuotesApi
import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord
import com.example.donemate.data.remote.model.RemoteQuotesList
import com.example.donemate.data.repository.remote.AccountRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

private const val TAG = "OfflineFirstQuotesRepository"

class OfflineFirstQuotesRepository
@Inject constructor(
    private val quotesApi: QuotesApi,
    private val quotesLocalDataSource: QuotesLocalDataSource,
    private val accountRepository: AccountRepository,
    private val likedQuoteRecordsRemoteDataSource: LikedQuoteRecordsRemoteDataSource
) : QuotesRepository {
    override suspend fun getNetworkQuotesList(page: Int): Result<RemoteQuotesList> =
        safeCall("getNetworkQuotesList", TAG) {
            quotesApi.getQuotes(page)
        }

    // I didn't find a way to make error handling for this method
    override fun getAllQuotesPagingSource(): PagingSource<Int, QuoteEntity> {
        return quotesLocalDataSource.pagingSource()
    }

    override suspend fun clearAll() =
        safeCall("getNetworkQuotesList", TAG) {
            quotesLocalDataSource.clearAll()
        }

    override suspend fun insertAll(quotes: List<QuoteEntity>) =
        safeCall("insertAll", TAG) {
            quotesLocalDataSource.insertAll(quotes)
        }

    override suspend fun markAsLiked(id: String) {
        safeCall("markAsLiked", TAG) {
            quotesLocalDataSource.markAsLiked(id, System.currentTimeMillis())
        }
    }

    override suspend fun unlike(id: String) {
        safeCall("unlike", TAG) {
            quotesLocalDataSource.unlike(id, System.currentTimeMillis())
        }
    }

    // TODO: Add error handling
    override val likedQuotes: Flow<Result<List<Quote>>> = flow {
        quotesLocalDataSource.getLikedQuotes().collect { entities ->
            emit(Result.Success(entities.map { it.asExternalModel() }))
        }
    }

    override suspend fun sync(): Result<Unit> {
        return safeCall("sync", TAG) {
            val remoteLikedQuoteRecords =
                likedQuoteRecordsRemoteDataSource.getLikedQuoteRecords(accountRepository.getUserId()!!)
            val localQuotesNeedingSync = quotesLocalDataSource.getQuotesNeedingSync().first()

            // Remote → Local
            remoteLikedQuoteRecords.forEach { remoteLikedQuoteRecord ->
                val localQuoteWithRemoteId =
                    localQuotesNeedingSync.firstOrNull { remoteLikedQuoteRecord.id == it.id }

                if (localQuoteWithRemoteId == null) {
                    quotesLocalDataSource.markAsLiked(remoteLikedQuoteRecord.id, System.currentTimeMillis())
                } else {
                    if (localQuoteWithRemoteId.updatedAt < remoteLikedQuoteRecord.updatedAt) {
                        if (!localQuoteWithRemoteId.isLiked) {
                            quotesLocalDataSource.markAsLiked(remoteLikedQuoteRecord.id, System.currentTimeMillis())
                        }
                    }
                }
            }

            // Local → Remote
            localQuotesNeedingSync.forEach { localLikedQuote ->
                val remoteLikedQuoteRecord =
                    remoteLikedQuoteRecords.firstOrNull { localLikedQuote.id == it.id }

                if (remoteLikedQuoteRecord == null) {
                    likedQuoteRecordsRemoteDataSource.saveLikedQuoteRecord(
                        RemoteLikedQuoteRecord(
                            localLikedQuote.id,
                            System.currentTimeMillis(),
                            accountRepository.getUserId()!!
                        ),
                    )
                }
                else if (localLikedQuote.updatedAt > remoteLikedQuoteRecord.updatedAt) {
                    likedQuoteRecordsRemoteDataSource.updateLikedQuoteRecord(
                        RemoteLikedQuoteRecord(
                            localLikedQuote.id,
                            System.currentTimeMillis(),
                            accountRepository.getUserId()!!
                        )
                    )
                }
            }

            localQuotesNeedingSync.forEach { local ->
                val remote = remoteLikedQuoteRecords.find { local.id == it.id }

                if (remote == null && !local.needsSync) {
                    quotesLocalDataSource.delete(local)
                }
            }
        }
    }
}