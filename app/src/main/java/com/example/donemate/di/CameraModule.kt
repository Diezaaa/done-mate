package com.example.donemate.di

import android.app.Application
import com.example.donemate.domain.camera.CameraSystem
import com.example.donemate.data.camera.CameraXCameraSystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {
    @Provides
    @Singleton
    fun realCameraSystem(
        application: Application
    ): CameraSystem {
        return CameraXCameraSystem(application)
    }
}