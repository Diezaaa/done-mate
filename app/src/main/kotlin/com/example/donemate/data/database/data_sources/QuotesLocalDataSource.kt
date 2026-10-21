package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import com.example.donemate.data.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

interface QuotesLocalDataSource {
    suspend fun upsertAll(quoteEntities: List<QuoteEntity>)
    suspend fun upsert(quoteEntity: QuoteEntity)
    fun pagingSource(): PagingSource<Int, QuoteEntity>
    suspend fun markAsUnliked(id: String, updatedAt: Long)
    suspend fun markAsUnlikedAndNeedsSync(id: String, updatedAt: Long)
    suspend fun markAsLikedAndNeedsSync(id: String, updatedAt: Long)
    suspend fun getAllQuotes(): List<QuoteEntity>
    suspend fun clearAll()
    suspend fun markAsLiked(id: String, updatedAt: Long)
    suspend fun getQuotesNeedingSync(): List<QuoteEntity>
    suspend fun markAsNeedsSync(id: String)
    suspend fun markAsNotNeedsSync(id: String)
    fun getLikedQuotes(): Flow<List<QuoteEntity>>
}