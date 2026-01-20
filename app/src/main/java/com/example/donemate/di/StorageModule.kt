package com.example.donemate.di

import com.example.donemate.data.local.AndroidImageStorage
import com.example.donemate.domain.data.local.ImageStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindImageStorage(
        impl: AndroidImageStorage
    ): ImageStorage
}