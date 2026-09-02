package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import com.example.donemate.data.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

interface QuotesLocalDataSource {
    suspend fun insertAll(quoteEntities: List<QuoteEntity>)
    suspend fun insert(quoteEntity: QuoteEntity)
    fun pagingSource(): PagingSource<Int, QuoteEntity>
    suspend fun unlike(id: String, updatedAt: Long)
    suspend fun clearAll()
    suspend fun markAsLiked(id: String, updatedAt: Long)
    fun getQuotesNeedingSync(): Flow<List<QuoteEntity>>
    suspend fun markAsNeedsSync(id: String, updatedAt: Long)
    fun getLikedQuotes(): Flow<List<QuoteEntity>>
    fun delete(quoteEntity: QuoteEntity)
}