package com.example.donemate.ui.screens.update_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UpdatePasswordScreen(
    vm: UpdatePasswordViewModel,
    onShowSnackbar: suspend (String) -> SnackbarResult,
    onNavigateToSettings: () -> Unit
) {

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { onShowSnackbar(it) }
        vm.errorMessageShown()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess == true) {
            onNavigateToSettings()
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .width(300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Change Password",

                fontSize = 35.sp
            )
            TextField(
                value = uiState.currentPassword,
                onValueChange = vm::updateCurrentPassword,
                label = { Text("Current password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            TextField(
                value = uiState.newPassword,
                onValueChange = vm::updateNewPassword,
                label = { Text("New password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    vm.updatePassword()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change password")
            }
        }

    }
}