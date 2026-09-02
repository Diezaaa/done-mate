package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteQuotesList

val motivationalOr = listOf(
    "motivational",
    "inspirational",
    "success",
    "perseverance",
    "self-help",
    "wisdom",
    "courage",
    "work",
    "change",
    "opportunity"
).joinToString("|")

interface QuotesApi {
    // TODO: Add error handling in the impl to expose errors
    suspend fun getQuotes(
        page: Int = 1,
        tags: String = motivationalOr,
        limit: Int = 20,
        sortBy: String = "dateAdded",
        order: String = "asc"
    ): RemoteQuotesList
}
