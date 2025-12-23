package com.example.donemate.ui.screens.sign_up

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.model.service.impl.AuthResult
import androidx.compose.material3.SnackbarHost
import com.example.donemate.ui.common.AuthenticationButton
import com.example.donemate.ui.common.launchCredManBottomSheet


@Composable
fun SignUpScreen(navigateToSignIn: () -> Unit, vm: SignUpViewModel, navigateToTasks: () -> Boolean){
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val isLogged by vm.hasUser.collectAsStateWithLifecycle(false)
    val authState by vm.authState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.NotStarted -> {}
            is AuthResult.Success -> {}
            is AuthResult.Error -> snackBarHostState.showSnackbar((authState as AuthResult.Error).message, duration = SnackbarDuration.Indefinite)
        }
    }


    LaunchedEffect(isLogged) {
        if(isLogged) {
            navigateToTasks()
        }
    }

    LaunchedEffect(Unit) {
        launchCredManBottomSheet(context) { result ->
            vm.onSignUpWithGoogle(result)
        }
    }

    Scaffold(
        snackbarHost = {
            Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter) {
                SnackbarHost(hostState = snackBarHostState)
            }
        }) { paddingValues ->
        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.width(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sign Up",
                    fontSize = 35.sp
                )
                Spacer(Modifier.height(15.dp))

                TextField(
                    value = uiState.email,
                    onValueChange = vm::onEmailChange,
                    label = { Text("E-mail") }
                )
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = uiState.password,
                    onValueChange = vm::onPasswordChange,
                    label = { Text("Password") }
                )
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { vm.onSignUpClick() },
                    modifier = Modifier.width(283.dp)
                ) {
                    Text("Sign up")
                }

                Spacer(Modifier.height(8.dp))
                Text("OR")
                Spacer(Modifier.height(8.dp))
                AuthenticationButton("Sign in with Google") { credential ->
                    vm.onSignUpWithGoogle(credential)
                }
                Text(text = "Continue without account", modifier = Modifier.clickable {
                    vm.onContinueAnonymously()
                })
                Spacer(Modifier.height(8.dp))
                Text(text = "Go to sign in", modifier = Modifier.clickable {
                    navigateToSignIn()
                })
            }
        }
    }
}