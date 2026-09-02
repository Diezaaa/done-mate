package com.example.donemate.ui.screens.sign_up

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.ui.component.AuthForm
import com.example.donemate.ui.component.ContinueWithGoogleButton
import com.example.donemate.ui.component.launchContinueWithGoogleBottomSheet
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SignUpScreen(
    vm: SignUpViewModel,
    onNavigateToSignIn: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onShowSnackbar: suspend (String) -> SnackbarResult,
    onNavigateToVerifyEmail: (String) -> Unit,
) {

    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(2.seconds)
        launchContinueWithGoogleBottomSheet(context) { result ->
            vm.continueWithGoogle(result)
        }
    }

    LaunchedEffect(uiState.authForm.errorMessage) {
        if (uiState.authForm.errorMessage != null) {
            onShowSnackbar("Something went wrong")
            vm.errorMessageShown()
        }
    }

    LaunchedEffect(uiState.isUserVerified) {
        if (uiState.isUserVerified == true) {
            onNavigateToTasks()
        } else if (uiState.isUserVerified == false){
            onNavigateToVerifyEmail(uiState.authForm.email)
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .width(300.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Sign Up",
                fontSize = 35.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthForm(
                email = uiState.authForm.email,
                password = uiState.authForm.password,
                onEmailChange = vm::updateEmail,
                onPasswordChange = vm::updatePassword,
            )
            Button(
                onClick = {
                    vm.createUserWithEmailAndPassword()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = 24.dp),
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            ContinueWithGoogleButton("Continue with Google") {
                vm.continueWithGoogle(it)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Already have an account?",
                modifier = Modifier.clickable {
                    onNavigateToSignIn()
                })
        }
    }
}
