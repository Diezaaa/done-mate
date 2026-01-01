package com.example.donemate.model.service.impl

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.util.Rational
import android.view.Surface
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.donemate.model.service.CameraSystem
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

class CameraXCameraSystem @Inject constructor(
    private val application: Application
): CameraSystem {
    private lateinit var cameraProvider: ProcessCameraProvider
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo?  = null
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)

    override fun getSurfaceRequest(): StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()
    override fun toggleTorch() {
        if (cameraInfo?.torchState?.value == TorchState.ON){
            cameraControl?.enableTorch(false)
        }
        else{
            cameraControl?.enableTorch(true)
        }
    }

    override fun setZOOM(scaleFactor: Float) {
        val currentZoom = cameraInfo?.zoomState?.value?.zoomRatio
        val newZoom = (currentZoom?.times(scaleFactor))?.coerceIn(cameraInfo?.zoomState?.getValue()?.minZoomRatio, cameraInfo?.zoomState?.getValue()?.maxZoomRatio)
        cameraControl?.setZoomRatio(newZoom ?: 1f)
    }

    private val imageCaptureUseCase = ImageCapture.Builder()
        .setTargetRotation(Surface.ROTATION_90)
        .build()
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    private val viewPort = ViewPort.Builder(Rational(1,1), Surface.ROTATION_0)
        .setScaleType(ViewPort.FILL_CENTER)
        .build()

    private val useCaseGroup = UseCaseGroup.Builder()
        .setViewPort(viewPort)
        .addUseCase(cameraPreviewUseCase)
        .addUseCase(imageCaptureUseCase)
        .build()

    override suspend fun startCamera(
        lifecycleOwner: LifecycleOwner
    ) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(application)
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, useCaseGroup
        )
        cameraControl = camera.cameraControl
        cameraInfo = camera.cameraInfo

    }


}