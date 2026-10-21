package com.example.donemate.ui.screens.preview

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.donemate.camera.CameraSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val cameraSystem: CameraSystem
) : ViewModel() {

    val surfaceRequest = cameraSystem.getSurfaceRequest()
    val uiState = cameraSystem.getCurrentCameraState()

    suspend fun bindToCamera(lifecycleOwner: LifecycleOwner){
        cameraSystem.startCamera(lifecycleOwner)
    }

    fun toggleTorch(){
        cameraSystem.toggleTorch()
    }

    fun setZoom(scaleFactor: Float){
        cameraSystem.setZoom(scaleFactor)
    }

    fun focus(transform: Offset) {
        cameraSystem.focus(transform)
    }

    // TODO: This function violates the UDF pattern. Consider fixing it
    suspend fun capture(): Uri {
        return cameraSystem.capture()
    }
}

