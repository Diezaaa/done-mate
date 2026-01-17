package com.example.donemate.ui.screens.preview

import android.net.Uri
import android.util.Log
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.local.TaskPicturesRepositoryImpl
import com.example.donemate.data.local.models.TaskPicture
import com.example.donemate.domain.camera.CameraSystem
import com.example.donemate.domain.remote.MediaStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

