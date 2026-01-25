package com.example.donemate.di

import com.example.donemate.domain.remote.AccountRepository
import com.example.donemate.domain.remote.StorageRepository
import com.example.donemate.domain.remote.FirestoreRepository
import com.example.donemate.data.remote.AccountRepositoryImpl
import com.example.donemate.data.remote.StorageRepositoryImpl
import com.example.donemate.data.remote.FirestoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds
  abstract fun provideAccountService(impl: AccountRepositoryImpl): AccountRepository
    @Binds
    abstract fun provideStorageService(impl: FirestoreRepositoryImpl): FirestoreRepository
    @Binds
    abstract fun provideMediaService(impl: StorageRepositoryImpl): StorageRepository
}