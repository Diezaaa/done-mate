package com.example.donemate.di

import com.example.donemate.data.repository.OfflineFirstTasksRepositoryImpl
import com.example.donemate.domain.data.repository.OfflineFirstTasksRepository
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
}