package com.example.donemate.model.service

import android.content.Context
import androidx.camera.core.CameraState
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow

interface CameraSystem {
    suspend fun startCamera(lifecycleOwner: LifecycleOwner)
    fun getSurfaceRequest(): StateFlow<SurfaceRequest?>
    fun toggleTorch()

    fun setZOOM(scaleFactor: Float)
    fun getCurrentCameraState(): StateFlow<CameraState>

}