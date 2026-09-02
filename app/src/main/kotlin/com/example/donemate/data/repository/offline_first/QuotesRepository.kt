package com.example.donemate.data.repository.offline_first

import androidx.paging.PagingSource
import com.example.donemate.data.common.Result
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.model.Quote
import com.example.donemate.data.remote.model.RemoteQuotesList
import kotlinx.coroutines.flow.Flow

interface QuotesRepository{
    suspend fun getNetworkQuotesList(page: Int): Result<RemoteQuotesList>
    fun getAllQuotesPagingSource(): PagingSource<Int, QuoteEntity>
    suspend fun clearAll(): Result<Unit>
    suspend fun markAsLiked(id: String)
    suspend fun unlike(id: String)
    suspend fun insertAll(quotes: List<QuoteEntity>): Result<Unit>
    val likedQuotes: Flow<Result<List<Quote>>>
    suspend fun sync(): Result<Unit>
}