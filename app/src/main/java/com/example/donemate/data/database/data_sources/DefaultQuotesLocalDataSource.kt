package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.donemate.data.database.model.QuoteEntity

/*
 * Room generates the implementation of this DAO at compile time.
 * This DAO manages quote data in the local database.
 * It is also used by Paging 3 as the local data source for the UI.
 */
@Dao
interface DefaultQuotesLocalDataSource: QuotesLocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(users: List<QuoteEntity>)

    @Query("SELECT * FROM quoteentity")
    override fun pagingSource(): PagingSource<Int, QuoteEntity>

    @Query("DELETE FROM quoteentity")
    override suspend fun clearAll()
}