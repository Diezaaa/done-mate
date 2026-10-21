package com.example.donemate.data.repository.offline_first

import androidx.paging.PagingSource
import com.example.donemate.data.common.Result
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.model.Quote
import com.example.donemate.data.remote.model.RemoteQuotesList
import kotlinx.coroutines.flow.Flow

interface QuotesRepository{
    suspend fun getRemoteQuotesList(page: Int): Result<RemoteQuotesList>
    fun getAllQuotesPagingSource(): PagingSource<Int, QuoteEntity>
    suspend fun clearAll(): Result<Unit>
    suspend fun markAsLiked(id: String)
    suspend fun markAsUnliked(id: String)
    suspend fun upsertAll(quotes: List<QuoteEntity>): Result<Unit>
    suspend fun upsert(quote: QuoteEntity)
    val likedQuotes: Flow<Result<List<Quote>>>
    suspend fun sync(): Result<Unit>
}