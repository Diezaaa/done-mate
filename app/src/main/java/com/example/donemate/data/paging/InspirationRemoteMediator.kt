package com.example.donemate.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.donemate.data.local.db.QuotesDao
import com.example.donemate.data.local.db.RemoteKey
import com.example.donemate.data.local.db.RemoteKeyDao
import com.example.donemate.data.local.model.QuoteEntity
import com.example.donemate.data.remote.model.asEntity
import com.example.donemate.domain.local.QuotesRepository

@OptIn(ExperimentalPagingApi::class)
class InspirationRemoteMediator(
    private val quotesDao: QuotesDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val quotesRepository: QuotesRepository
) : RemoteMediator<Int, QuoteEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QuoteEntity>
    ): MediatorResult {
        return try {

            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1

                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getRemoteKey()


                    if (remoteKey!!.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            val response = quotesRepository.getQuotes(page = loadKey)


            if (loadType == LoadType.REFRESH) {
                quotesDao.clearAll()
            }


            val nextKey = if (response.page < response.totalPages) {
                response.page + 1
            } else {
                null
            }

            remoteKeyDao.insertOrReplace(
                RemoteKey(id=1,response.page, nextKey)
            )

            val quotes =                 response.results.map { networkQuote ->
                networkQuote.asEntity()
            }

            quotesDao.insertAll(
                quotes
            )

            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}