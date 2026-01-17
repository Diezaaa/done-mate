package com.example.donemate.di

import com.example.donemate.domain.remote.AccountService
import com.example.donemate.domain.remote.MediaStorage
import com.example.donemate.domain.remote.StorageService
import com.example.donemate.data.remote.AccountServiceImpl
import com.example.donemate.data.remote.MediaStorageImpl
import com.example.donemate.data.remote.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds
  abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
    @Binds
    abstract fun provideMediaService(impl: MediaStorageImpl): MediaStorage
}