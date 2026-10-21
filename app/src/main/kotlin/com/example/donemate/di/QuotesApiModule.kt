package com.example.donemate.di

import com.example.donemate.data.remote.DefaultQuotesApi
import com.example.donemate.data.remote.QuotesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object QuotesApiModule {

    const val BASE_URL = "http://api.quotable.io"

    @Provides
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun providesQuotesApi(
        retrofit: Retrofit
    ): QuotesApi {
        return retrofit.create(DefaultQuotesApi::class.java)
    }
}