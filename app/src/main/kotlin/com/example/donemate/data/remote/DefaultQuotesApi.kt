package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteQuotesList
import retrofit2.http.GET
import retrofit2.http.Query


interface DefaultQuotesApi: QuotesApi {
    @GET("quotes")
    override suspend fun getQuotes(
        @Query("page") page: Int,
        @Query("tags") tags: String,
        @Query("limit") limit: Int,
        @Query("sortBy") sortBy: String,
        @Query("order") order: String
    ): RemoteQuotesList
}