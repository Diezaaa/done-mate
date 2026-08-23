package com.example.donemate.di

import com.example.donemate.data.image.AndroidImageStorage
import com.example.donemate.data.image.ImageStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    abstract fun bindImageStorage(
        androidImageStorage: AndroidImageStorage
    ): ImageStorage
}