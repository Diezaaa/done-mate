package com.example.donemate.model.service.module

import android.app.Application
import com.example.donemate.model.service.CameraSystem
import com.example.donemate.model.service.impl.CameraXCameraSystem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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