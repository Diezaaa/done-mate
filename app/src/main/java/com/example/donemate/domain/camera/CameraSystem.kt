package com.example.donemate.domain.camera

import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.donemate.data.camera.CameraState
import kotlinx.coroutines.flow.StateFlow

interface CameraSystem {
    suspend fun startCamera(lifecycleOwner: LifecycleOwner)
    fun getSurfaceRequest(): StateFlow<SurfaceRequest?>
    fun toggleTorch()
    fun setZOOM(scaleFactor: Float)
    fun getCurrentCameraState(): StateFlow<CameraState>

    fun focus(offset: Offset)
    suspend fun onCapture(): Uri
}