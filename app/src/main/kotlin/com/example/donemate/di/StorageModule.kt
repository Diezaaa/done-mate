package com.example.donemate.di

import com.example.donemate.data.image.AndroidTaskImagesLocalDataSource
import com.example.donemate.data.image.TaskImagesLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun bindsImageStorage(
        androidImageStorage: AndroidTaskImagesLocalDataSource
    ): TaskImagesLocalDataSource
}