package com.example.donemate.di

import com.example.donemate.domain.data.remote.AccountService
import com.example.donemate.domain.data.remote.StorageNetwork
import com.example.donemate.domain.data.remote.FirestoreService
import com.example.donemate.data.remote.AccountServiceImpl
import com.example.donemate.data.remote.StorageServiceImpl
import com.example.donemate.data.remote.FirestoreServiceImpl
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
    abstract fun provideStorageService(impl: FirestoreServiceImpl): FirestoreService
    @Binds
    abstract fun provideMediaService(impl: StorageServiceImpl): StorageNetwork
}