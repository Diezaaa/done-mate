package com.example.donemate.ui.screens.change_email

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
fun ChangeEmailScreen(
    vm: ChangeEmailViewModel,
    onShowSnackbar: suspend (String) -> SnackbarResult,
    navigateToVerifyEmail: (String) -> Boolean
) {

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.shouldRedirectToVerification) {
        if (uiState.shouldRedirectToVerification) {
            vm.changeShouldRedirectToVerification(false)
            navigateToVerifyEmail(uiState.email)
        }
    }
    LaunchedEffect(uiState.isFailure) {
        if (uiState.isFailure == true) {
            onShowSnackbar(uiState.errorMessage ?: "Something went wrong")
            vm.errorMessageShown()
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
                "Change Email",
                fontSize = 35.sp
            )
            Text("You'll be signed out of your current account after verification")
            TextField(
                value = uiState.email,
                onValueChange = vm::updateEmail,
                label = { Text("New email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    vm.changeEmail()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change email")
            }
        }
    }
}

