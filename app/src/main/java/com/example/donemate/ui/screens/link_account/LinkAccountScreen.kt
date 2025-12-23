package com.example.donemate.ui.screens.link_account

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.SnackbarHost
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
import com.example.donemate.ui.screens.sign_up.SignUpViewModel
import kotlinx.coroutines.delay

@Composable
fun LinkAccountScreen(navigateToTasks: () -> Unit, vm: LinkAccountViewModel){
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val currentUser by vm.currentUser.collectAsStateWithLifecycle(null)
    val authState by vm.authState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current



    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.NotStarted -> {}
            is AuthResult.Success -> {}
            is AuthResult.Error -> snackBarHostState.showSnackbar((authState as AuthResult.Error).message, duration = SnackbarDuration.Indefinite)
        }
        delay(2_000)
        if(authState is AuthResult.Success) {
            navigateToTasks()
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
                    "Link account",
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
                    onClick = { vm.onLinkAccount(currentUser) },
                    modifier = Modifier.width(283.dp)
                ) {
                    Text("Link")
                }
            }
        }
    }
}