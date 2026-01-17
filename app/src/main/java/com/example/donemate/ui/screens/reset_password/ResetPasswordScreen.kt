package com.example.donemate.ui.screens.reset_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ResetPasswordScreen(
    vm: ResetPasswordViewModel,
    navigateToSignIn: () -> Boolean
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
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
                onValueChange = vm::onEmailChange,
                label = { Text("E-mail") }
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.onForgotPassword() },
                modifier = Modifier.width(283.dp)
            ) {
                Text("Send code")
            }
            if (uiState.isSuccess){
                Text("Reset password e-mail was sent. Don't forget to check the spam folder too.")
            }
        }
    }
}