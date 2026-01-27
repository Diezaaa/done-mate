package com.example.donemate.data.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.donemate.data.local.model.QuoteEntity

@Dao
interface QuotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<QuoteEntity>)

    @Query("SELECT * FROM quoteentity")
    fun pagingSource(): PagingSource<Int, QuoteEntity>

    @Query("DELETE FROM quoteentity")
    suspend fun clearAll()
}