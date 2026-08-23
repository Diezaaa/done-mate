package com.example.donemate.data.database.data_sources

import androidx.paging.PagingSource
import com.example.donemate.data.database.model.QuoteEntity

interface QuotesLocalDataSource {
    suspend fun insertAll(users: List<QuoteEntity>)

    fun pagingSource(): PagingSource<Int, QuoteEntity>

    suspend fun clearAll()
}