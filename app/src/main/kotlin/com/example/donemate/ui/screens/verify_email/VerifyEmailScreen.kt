package com.example.donemate.ui.screens.verify_email

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.data.model.User
import com.google.firebase.auth.EmailAuthProvider

@Composable
fun VerifyEmailScreen(
    viewModel: VerifyEmailViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateBack: () -> Unit,
    emailToVerify: String?,
    onNavigateToSignUp: () -> Unit,
) {
    val uiState by viewModel.user.collectAsStateWithLifecycle()

    VerifyEmailScreen(
        uiState = uiState,
        onNavigateToTasks = onNavigateToTasks,
        onNavigateBack = onNavigateBack,
        onUnlinkAuthProvidersExceptEmail = viewModel::unlinkAuthProvidersExceptEmail,
        emailToVerify = emailToVerify,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun VerifyEmailScreen(
    uiState: VerifyEmailUiState,
    onNavigateToTasks: () -> Unit,
    onNavigateBack: () -> Unit,
    onUnlinkAuthProvidersExceptEmail: () -> Unit,
    emailToVerify: String?,
    onNavigateToSignUp: () -> Unit,
) {

    uiState.let { uiState ->
        if (uiState is VerifyEmailUiState.Success) {
            uiState.user.let { user ->
                LaunchedEffect(user) {
                    if (user?.isEmailVerified ?: false && user.email == emailToVerify) {
                        onUnlinkAuthProvidersExceptEmail()
                        onNavigateToTasks()
                    }
                }

                /* TODO: I think this code is redundant because there's no point in navigating the user back when there's no email.
                    Note that the edit email button doesn't sign out. Figure out it later */
                LaunchedEffect(user) {
                    if (user?.authProviders?.contains(EmailAuthProvider.PROVIDER_ID) == false) {
                        onNavigateBack()
                    }
                }

                LaunchedEffect(user) {
                    if (user == null) {
                        onNavigateToSignUp()
                    }
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(270.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                "Verify your email", fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                "We have sent an email with a verification link to your email address. Don't forget to check the spam folder too.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Email")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    VerifyEmailScreen(
        onNavigateToTasks = {},
        onNavigateBack = {},
        onUnlinkAuthProvidersExceptEmail = {},
        emailToVerify = null,
        uiState = VerifyEmailUiState.Success(
            User()
        ),
        onNavigateToSignUp = {}
    )
}