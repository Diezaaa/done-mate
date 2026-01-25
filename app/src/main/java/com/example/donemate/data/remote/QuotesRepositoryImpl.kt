package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.NetworkQuotesList
import com.example.donemate.domain.local.QuotesRepository
import jakarta.inject.Inject

class QuotesRepositoryImpl
    @Inject constructor(
        private val api: QuoteApi
    ): QuotesRepository {
    override suspend fun getQuotes(page: Int): NetworkQuotesList {
       return api.getQuotes(page)
    }
}