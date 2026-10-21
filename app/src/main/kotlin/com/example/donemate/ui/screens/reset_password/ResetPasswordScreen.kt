package com.example.donemate.ui.screens.reset_password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    onShowSnackbar: suspend (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            onShowSnackbar("Something went wrong")
            viewModel.errorMessageShown()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Reset Password",
                fontSize = 35.sp
            )
            Spacer(Modifier.height(15.dp))

            TextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.sendPasswordResetEmail() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send code")
            }
            if (uiState.isSent) {
                Text("Reset password e-mail was sent. Don't forget to check the spam folder too.")
            }
        }
    }
}