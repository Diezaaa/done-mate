package com.example.donemate.data.camera

import android.app.Application
import android.net.Uri
import android.util.Rational
import android.view.Surface
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.takePicture
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.example.donemate.domain.camera.CameraSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class CameraState(
    val torchOn: Boolean = false,
    val zoomRatio: Float = 1f
)

class CameraXCameraSystem @Inject constructor(
    private val application: Application
): CameraSystem {
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo?  = null
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private val extensionsManager: ExtensionsManager? = null
    private val _cameraState = MutableStateFlow(CameraState())
    override fun getCurrentCameraState(): StateFlow<CameraState> = _cameraState.asStateFlow()
    override fun focus(offset: Offset) {
        val point = surfaceMeteringPointFactory?.createPoint(offset.x, offset.y)
        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }

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
        .setTargetRotation(Surface.ROTATION_0)
        .build()
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
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

        cameraInfo?.let { info ->
        // torchState: LiveData<Int> -> Flow<Int>
        val torchFlow = info.torchState.asFlow().map { it == TorchState.ON }

        // zoomState: LiveData<ZoomState> -> Flow<Float> of zoomRatio
        val zoomFlow = info.zoomState.asFlow().map { it.zoomRatio }

        // combine the two flows and push to _cameraState
        combine(torchFlow, zoomFlow) { torchOn, zoomRatio ->
            CameraState(torchOn = torchOn, zoomRatio = zoomRatio)
        }.onEach { newState ->
            _cameraState.value = newState
        }.launchIn(lifecycleOwner.lifecycleScope)
        }
    }
    // UNDERSTAND THIS CODE
    override suspend fun onCapture(): Uri = suspendCancellableCoroutine { cont ->
        val file = File(
            application.filesDir,
            "IMG_${System.currentTimeMillis()}.jpg"
        ).apply { parentFile?.mkdirs() }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        // use main executor to avoid creating a thread each capture
        val executor = ContextCompat.getMainExecutor(application)

        imageCaptureUseCase.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // CameraX may set savedUri (when using MediaStore); fall back to file URI.
                    val uri = output.savedUri ?: file.toUri()
                    if (cont.isActive) cont.resume(uri)
                }
            }
        )

    }
}