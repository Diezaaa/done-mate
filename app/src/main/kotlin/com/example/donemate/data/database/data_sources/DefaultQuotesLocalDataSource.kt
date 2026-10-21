package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.donemate.data.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

/*
 * Room generates the implementation of this DAO at compile time.
 * This DAO manages quote data in the local database.
 * It is also used by Paging 3 as the local data source for the UI.
 */
@Dao
interface DefaultQuotesLocalDataSource : QuotesLocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsertAll(quoteEntities: List<QuoteEntity>)

    @Query("SELECT * FROM quoteentity")
    override fun pagingSource(): PagingSource<Int, QuoteEntity>

    @Query("DELETE FROM quoteentity")
    override suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(quoteEntity: QuoteEntity)

    @Query(
        """UPDATE quoteentity SET isLiked = 1,
            updatedAt = :updatedAt
            WHERE id = :id"""
    )
    override suspend fun markAsLiked(id: String, updatedAt: Long)

    @Query("UPDATE quoteentity SET isLiked = 0, updatedAt = :updatedAt WHERE id = :id")
    override suspend fun markAsUnliked(id: String, updatedAt: Long)

    @Transaction
    @Query("UPDATE quoteentity SET isLiked = 0, updatedAt = :updatedAt, needsSync = 1 WHERE id = :id")
    override suspend fun markAsUnlikedAndNeedsSync(id: String, updatedAt: Long)

    @Transaction
    @Query("UPDATE quoteentity SET isLiked = 1, updatedAt = :updatedAt, needsSync = 1 WHERE id = :id")
    override suspend fun markAsLikedAndNeedsSync(id: String, updatedAt: Long)

    @Query("SELECT * FROM quoteentity WHERE needsSync = 1")
    override suspend fun getQuotesNeedingSync(): List<QuoteEntity>

    @Query("SELECT * FROM quoteentity")
    override suspend fun getAllQuotes(): List<QuoteEntity>

    @Query("UPDATE quoteentity SET needsSync = 1 WHERE id = :id")
    override suspend fun markAsNeedsSync(id: String)

    @Query("UPDATE quoteentity SET needsSync = 0 WHERE id = :id")
    override suspend fun markAsNotNeedsSync(id: String)

    @Query("SELECT * FROM quoteentity WHERE isLiked = 1")
    override fun getLikedQuotes(): Flow<List<QuoteEntity>>
}