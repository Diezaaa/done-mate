package com.example.donemate.di

import com.example.donemate.data.remote.QuoteApi
import com.example.donemate.data.shared.OfflineFirstTasksRepositoryImpl
import com.example.donemate.domain.repository.OfflineFirstTasksRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
 class QuoteApiModule {

    val BASE_URL = "http://api.quotable.io"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): QuoteApi =
        retrofit.create(QuoteApi::class.java)
}