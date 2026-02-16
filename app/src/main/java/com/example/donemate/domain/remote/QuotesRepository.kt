package com.example.donemate.domain.remote

import com.example.donemate.data.remote.model.NetworkQuotesList

interface QuotesRepository{
    suspend fun getQuotes(page: Int): NetworkQuotesList
}