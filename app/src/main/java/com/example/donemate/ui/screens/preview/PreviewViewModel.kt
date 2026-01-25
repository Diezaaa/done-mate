package com.example.donemate.ui.screens.preview

import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.donemate.domain.camera.CameraSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val cameraSystem: CameraSystem)  : ViewModel() {

    private val _surfaceRequest = cameraSystem.getSurfaceRequest()
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private val _uiState = cameraSystem.getCurrentCameraState()
    val uiState = _uiState

    suspend fun bindToCamera(lifecycleOwner: LifecycleOwner){
        cameraSystem.startCamera(lifecycleOwner)
    }

    fun toggleTorch(){
        cameraSystem.toggleTorch()
    }

    fun setZoom(scaleFactor: Float){
        cameraSystem.setZOOM(scaleFactor)
    }

    fun tapToFocus(transform: Offset) {
        cameraSystem.focus(transform)
    }
    suspend fun capture(): Uri {
        // DOES IT RUN ON THE MAIN THREAD
        return cameraSystem.onCapture()
    }
}

