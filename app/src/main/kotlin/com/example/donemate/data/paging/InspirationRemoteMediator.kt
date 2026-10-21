package com.example.donemate.data.paging

import android.util.Log
import com.example.donemate.data.common.Result
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.donemate.data.database.model.RemoteKeyEntity
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.model.asEntity
import com.example.donemate.data.repository.local.RemoteKeyRepository
import com.example.donemate.data.repository.offline_first.QuotesRepository

// TODO: Maybe I should move to Kotlin's result class because it will make the code cleaner (without the when)
// TODO: Learn more about Paging and make the last quote viewed appear as the first after process death
@OptIn(ExperimentalPagingApi::class)
class InspirationRemoteMediator(
    private val quotesRepository: QuotesRepository,
    private val remoteKeyRepository: RemoteKeyRepository,
) : RemoteMediator<Int, QuoteEntity>() {

    override suspend fun initialize(): InitializeAction {
        return when (val result = remoteKeyRepository.getRemoteKey()) {
            is Result.Success -> {
                if (result.data == null) {
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                } else {
                    InitializeAction.SKIP_INITIAL_REFRESH
                }
            }

            is Result.Error -> {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QuoteEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1

                LoadType.APPEND -> {
                    val remoteKey = when (
                        val result = remoteKeyRepository.getRemoteKey()
                    ) {
                        is Result.Success -> result.data

                        is Result.Error -> {
                            return MediatorResult.Error(
                                Exception(result.message)
                            )
                        }
                    }

                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
            }

            val response = when (
                val result = quotesRepository.getRemoteQuotesList(
                    page = loadKey
                )
            ) {
                is Result.Success -> result.data

                is Result.Error -> {
                    return MediatorResult.Error(
                        Exception(result.message)
                    )
                }
            }

            val quotes = response.results.map { networkQuote ->
                networkQuote.asEntity()
            }

            val nextKey = if (response.page < response.totalPages) {
                response.page + 1
            } else {
                null
            }

            if (loadType == LoadType.REFRESH) {
                when (val result = quotesRepository.clearAll()) {
                    is Result.Success -> Unit

                    is Result.Error -> {
                        return MediatorResult.Error(
                            Exception(result.message)
                        )
                    }
                }
            }

            when (val result = quotesRepository.upsertAll(quotes)) {
                is Result.Success -> Unit

                is Result.Error -> {
                    return MediatorResult.Error(
                        Exception(result.message)
                    )
                }
            }

            when (
                val result = remoteKeyRepository.insertOrReplace(
                    RemoteKeyEntity(
                        id = 1,
                        current = response.page,
                        nextKey = nextKey
                    )
                )
            ) {
                is Result.Success -> Unit

                is Result.Error -> {
                    return MediatorResult.Error(
                        Exception(result.message)
                    )
                }
            }

            Log.d(
                "paging",
                "Loaded ${quotes.size} quotes from page ${response.page}"
            )

            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        }catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
        // TODO: Consider to add  catch (exception: CancellationException) {
        //            throw exception
        //        }
    }
}