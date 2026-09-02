package com.example.donemate.ui.screens.preview

import android.net.Uri
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PreviewScreen(
    taskTitle: String,
    viewModel: PreviewViewModel,
    onNavigateToTask: (uri: Uri) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(lifecycleOwner)
    }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified) }
    val autofocusRequestId = autofocusRequest.first
    val showAutofocusIndicator = autofocusRequest.second.isSpecified
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }
    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(1000.milliseconds)
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }

    Column(Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth().padding(10.dp),
            contentAlignment = Alignment.Center,) {
            Text(
                "Snapping image for ${taskTitle.takeIf { it.isNotBlank() } ?: "unnamed task"}",
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Box(
            modifier = Modifier
                .weight(1f).fillMaxWidth()
            , contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .aspectRatio(1f)
                ) {
                    surfaceRequest?.let { request ->
                        val coordinateTransformer = remember { MutableCoordinateTransformer() }
                        CameraXViewfinder(
                            surfaceRequest = request,
                            coordinateTransformer = coordinateTransformer,
                            modifier = Modifier
                                .pointerInput(viewModel, coordinateTransformer) {
                                    // Detect tap for focus
                                    detectTapGestures { tapCoords ->
                                        with(coordinateTransformer) {
                                            viewModel.tapToFocus(tapCoords.transform())
                                        }
                                        autofocusRequest = UUID.randomUUID() to tapCoords
                                    }
                                }
                                .pointerInput(viewModel, coordinateTransformer) {
                                    detectTransformGestures { _, _, zoom, _ ->
                                        viewModel.setZoom(zoom)
                                    }
                                }
                                .clip(RoundedCornerShape(8.dp))
                                .aspectRatio(1f)
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showAutofocusIndicator,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                                .offset((-24).dp, (-24).dp)
                        ) {
                            Spacer(Modifier.border(2.dp, Color.White, CircleShape).size(48.dp))
                        }
                    }

                }
}

            }
            val coroutineScope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp),
            ) {
                // Centered button (on the left)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val uri = viewModel.capture()
                            Log.d("uri", uri.toString())
                            onNavigateToTask(uri)

                        }
                  },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Set this as task image")
                }

                Text(
                    text = "${"%.1f".format(uiState.zoomRatio.toDouble())}x",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Icon(
                    imageVector = if (uiState.torchOn) {
                        Icons.Default.FlashlightOn
                    } else {
                        Icons.Default.FlashlightOff
                    }, contentDescription = if (uiState.torchOn) "Flashlight On" else "Flashlight Off",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable{viewModel.toggleTorch()}
                        .align(Alignment.CenterStart)
                )
            }

    }
}
