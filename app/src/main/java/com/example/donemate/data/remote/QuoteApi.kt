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
        @Query("page") page: Int,
        @Query("tags") tags: String = motivationalOr): NetworkQuotesList
}