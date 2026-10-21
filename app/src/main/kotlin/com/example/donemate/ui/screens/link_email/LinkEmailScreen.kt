package com.example.donemate.ui.screens.link_email

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun LinkEmailScreen(
    viewModel: LinkEmailViewModel,
    onShowSnackbar: suspend (String) -> SnackbarResult,
    onNavigateToVerifyEmail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess == false) {
            onShowSnackbar("Something went wrong")
            viewModel.errorMessageShown()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        uiState.isSuccess?.let {
            if (it) {
                onNavigateToVerifyEmail(uiState.email)
            }
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
                "Link Email",
                fontSize = 35.sp
            )
            Spacer(Modifier.height(15.dp))

            TextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            TextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Password") }
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.linkEmail() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Link")
            }
        }
    }
}