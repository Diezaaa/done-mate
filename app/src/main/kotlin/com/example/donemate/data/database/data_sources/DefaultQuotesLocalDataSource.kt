package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.donemate.data.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

/*
 * Room generates the implementation of this DAO at compile time.
 * This DAO manages quote data in the local database.
 * It is also used by Paging 3 as the local data source for the UI.
 */
@Dao
interface DefaultQuotesLocalDataSource: QuotesLocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(quoteEntities: List<QuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(quoteEntity: QuoteEntity)

    @Query("SELECT * FROM quoteentity")
    override fun pagingSource(): PagingSource<Int, QuoteEntity>

    @Query("DELETE FROM quoteentity")
    override suspend fun clearAll()

    @Query("""UPDATE quoteentity SET isLiked = 1,
            updatedAt = :updatedAt,
            needsSync = 1
            WHERE id = :id""")
    override suspend fun markAsLiked(id: String, updatedAt: Long)

    @Query("UPDATE quoteentity SET isLiked = 0, updatedAt = :updatedAt, needsSync = 1 WHERE id = :id")
    override suspend fun unlike(id: String, updatedAt: Long)

    @Query("SELECT * FROM quoteentity WHERE needsSync = 1")
    override fun getQuotesNeedingSync(): Flow<List<QuoteEntity>>

    @Query("UPDATE quoteentity SET needsSync = 1, updatedAt = :updatedAt WHERE id = :id")
    override suspend fun markAsNeedsSync(id: String, updatedAt: Long)

    @Query("SELECT * FROM quoteentity WHERE isLiked = 1")
    override fun getLikedQuotes(): Flow<List<QuoteEntity>>

    @Delete
    override fun delete(quoteEntity: QuoteEntity)
}