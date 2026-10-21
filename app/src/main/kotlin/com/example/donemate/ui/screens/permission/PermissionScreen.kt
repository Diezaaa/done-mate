package com.example.donemate.ui.screens.permission

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(onNavigateToPreview: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            onNavigateToPreview()
        }
    }

    PermissionScreen(
        isGranted = cameraPermissionState.status.isGranted,
        shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
        onRequestPermission = {
            cameraPermissionState.launchPermissionRequest()
        }
    )
}

@Composable
fun PermissionScreen(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(145.dp),
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Camera icon"
        )
        Column(
            modifier = Modifier
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isGranted) {
                Text("Camera permission is granted")
            } else {
                val textToShow = if (shouldShowRationale) {
                    "The camera is important for this app. Please grant the permission in the settings."
                } else {
                    "Camera permission required for this feature to be available. Please grant the permission."
                }
                Text(textToShow)
            }
        }
        Spacer(Modifier.height(20.dp))
        // TODO: Add redirecting to camera permission setting when rationale is shown
        Button(onClick = onRequestPermission) {
            Text("Grant permission")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionRationalePreview() {
    PermissionScreen(
        isGranted = false,
        shouldShowRationale = true,
        onRequestPermission = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionNotGrantedPreview() {
    PermissionScreen(
        isGranted = false,
        shouldShowRationale = false,
        onRequestPermission = {}
    )
}
