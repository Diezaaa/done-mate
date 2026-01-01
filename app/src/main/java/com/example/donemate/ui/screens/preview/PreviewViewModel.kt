package com.example.donemate.ui.screens.preview

import android.R.attr.height
import android.R.attr.width
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.util.Rational
import android.view.Surface
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.CameraSystem
import com.example.donemate.ui.common.CameraScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val cameraSystem: CameraSystem
)  : ViewModel() {

    private val _surfaceRequest = cameraSystem.getSurfaceRequest()
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private val _uiState = MutableStateFlow(CameraScreenUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun bindToCamera(lifecycleOwner: LifecycleOwner){
        cameraSystem.startCamera(lifecycleOwner)
    }

    fun toggleTorch(){
        cameraSystem.toggleTorch()
    }

    fun setZoom(scaleFactor: Float){
        cameraSystem.setZOOM(scaleFactor)
    }
}

