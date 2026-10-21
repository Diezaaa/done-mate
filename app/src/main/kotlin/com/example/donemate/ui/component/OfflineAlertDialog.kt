package com.example.donemate.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OfflineAlertDialog(
    onOfflineDialogDismiss: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.SignalWifiConnectedNoInternet4,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        },
        title = {
            Text(text = "You're offline")
        },
        text = {
            Text(text = "Your tasks aren't synced with your other devices while you're offline!")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onOfflineDialogDismiss()
                }
            ) {
                Text("OK")
            }
        },
        onDismissRequest = {}
    )
}