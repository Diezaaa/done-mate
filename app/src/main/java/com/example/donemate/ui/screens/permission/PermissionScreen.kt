package com.example.donemate.ui.screens.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen() {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    PermissionScreenContent(
        isGranted = cameraPermissionState.status.isGranted,
        shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
        onRequestPermission = {
            cameraPermissionState.launchPermissionRequest()
        }
    )
}

@Composable
fun PermissionScreenContent(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isGranted) {
                Text("Camera permission Granted")
            } else {
                val textToShow = if (shouldShowRationale) {
                    "The camera is important for this app. Please grant the permission."
                } else {
                    "Camera permission required for this feature to be available. Please grant the permission"
                }

                Text(textToShow)
                Spacer(Modifier.height(20.dp))
            }
        }
        Button(onClick = onRequestPermission) {
            Text("Grant permission")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview(){
    PermissionScreen()
}

@Preview(showBackground = true)
@Composable
fun PermissionGrantedPreview() {
    PermissionScreenContent(
        isGranted = true,
        shouldShowRationale = false,
        onRequestPermission = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionRationalePreview() {
    PermissionScreenContent(
        isGranted = false,
        shouldShowRationale = true,
        onRequestPermission = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionDeniedPreview() {
    PermissionScreenContent(
        isGranted = false,
        shouldShowRationale = false,
        onRequestPermission = {}
    )
}
