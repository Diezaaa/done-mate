package com.example.donemate.di

import com.example.donemate.data.remote.QuotesRepositoryImpl
import com.example.donemate.data.shared.OfflineFirstTasksRepositoryImpl
import com.example.donemate.domain.local.QuotesRepository
import com.example.donemate.domain.repository.OfflineFirstTasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksRepositoryModule {

    @Binds
    abstract fun bindOfflineFirstTasksRepository(
        impl: OfflineFirstTasksRepositoryImpl
    ): OfflineFirstTasksRepository

    @Binds
    abstract fun bindQuotesRepository(
        impl: QuotesRepositoryImpl
    ): QuotesRepository
}