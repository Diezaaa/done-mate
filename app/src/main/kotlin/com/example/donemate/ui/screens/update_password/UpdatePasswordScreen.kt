package com.example.donemate.ui.screens.update_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.donemate.ui.common.FormState
import com.example.donemate.ui.component.CustomLoadingIndicator

@Composable
fun UpdatePasswordScreen(
    viewModel: UpdatePasswordViewModel,
    onShowSnackbar: suspend (String) -> SnackbarResult,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.formState) {
        uiState.let {
            if (it.formState is FormState.Error) {
                onShowSnackbar(it.formState.message)
                viewModel.errorMessageShown()
            }
        }
    }

    LaunchedEffect(uiState.formState) {
        if (uiState.formState is FormState.Success) {
            onNavigateToSettings()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
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
            if (uiState.formState is FormState.Submitting) {
                CustomLoadingIndicator()
            } else {
                Text(
                    "Change Password",
                    fontSize = 35.sp
                )
                TextField(
                    value = uiState.currentPassword,
                    onValueChange = viewModel::updateCurrentPassword,
                    label = { Text("Current password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                TextField(
                    value = uiState.newPassword,
                    onValueChange = viewModel::updateNewPassword,
                    label = { Text("New password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = {
                        viewModel.updatePassword()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change password")
                }
            }
        }
    }
}