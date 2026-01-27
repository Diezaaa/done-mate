package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.NetworkQuotesList
import retrofit2.http.GET
import retrofit2.http.Query
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
interface QuoteApi {
    @GET("quotes")
    suspend fun getQuotes(
        @Query("page") page: Int = 1,
        @Query("tags") tags: String = motivationalOr,
        @Query("limit") limit: Int = 20,
        @Query("sortBy") sortBy: String = "dateAdded",
        @Query("order") order: String = "asc"
    ): NetworkQuotesList
}