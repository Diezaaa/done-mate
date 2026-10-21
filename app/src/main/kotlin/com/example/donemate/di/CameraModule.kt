package com.example.donemate.di

import android.app.Application
import com.example.donemate.camera.CameraSystem
import com.example.donemate.camera.CameraXCameraSystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object CameraModule {
    @Provides
    fun providesCameraSystem(
        application: Application
    ): CameraSystem {
        return CameraXCameraSystem(application)
    }
}