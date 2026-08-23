package com.example.donemate.data.repository.offline_first

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.QuotesLocalDataSource
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.remote.QuotesRemoteDataSource
import com.example.donemate.data.remote.model.RemoteQuotesList
import jakarta.inject.Inject

private const val TAG = "OfflineFirstQuotesRepository"

class OfflineFirstQuotesRepository
@Inject constructor(
    private val quotesNetworkDataSource: QuotesRemoteDataSource,
    private val quotesLocalDataSource: QuotesLocalDataSource
) : QuotesRepository {
    override suspend fun getNetworkQuotesList(page: Int): Result<RemoteQuotesList> =
        safeCall("getNetworkQuotesList", TAG) {
            quotesNetworkDataSource.getQuotes(page)
        }

    // I didn't find a way to make error handling for this method
    override fun getQuotesPagingSource(): PagingSource<Int, QuoteEntity> {
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
}