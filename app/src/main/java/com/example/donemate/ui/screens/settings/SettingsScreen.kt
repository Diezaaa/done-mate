package com.example.donemate.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.Credential
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.data.model.User
import com.example.donemate.ui.component.ContinueWithGoogleButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    navigateToLinkEmail: () -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToChangeEmail: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    navigateToSignUp: () -> Unit
) {

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onDeleteAccountClick = vm::deleteAccount,
        onSignOutClick = vm::signOut,
        navigateToLinkEmail = navigateToLinkEmail,
        onThemeChange = vm::updateTheme,
        navigateToChangePassword = navigateToChangePassword,
        navigateToChangeEmail = navigateToChangeEmail,
        onShowSnackbar = onShowSnackbar,
        onErrorMessageShown = vm::errorMessageShown,
        onLinkGoogle = { credential: Credential ->
            vm.linkGoogle(credential)
        },
        navigateToSignUp = navigateToSignUp
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onDeleteAccountClick: () -> Unit,
    onSignOutClick: () -> Unit,
    navigateToLinkEmail: () -> Unit,
    onThemeChange: (isDarkTheme: Boolean) -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToChangeEmail: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    onErrorMessageShown: () -> Unit,
    onLinkGoogle: (credential: Credential) -> Unit,
    navigateToSignUp: () -> Unit
) {


    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            onShowSnackbar(uiState.errorMessage)
            onErrorMessageShown()
        }
    }

    LaunchedEffect(uiState.userProfile) {
        if (uiState.userProfile == null) {
            navigateToSignUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Account", fontSize = 20.sp, fontWeight = FontWeight.Bold)


        if (uiState.userProfile?.isAnonymous == true) {
            Text("You are currently signed in anonymously.")
        } else if (uiState.userProfile?.authProviders?.contains(GoogleAuthProvider.PROVIDER_ID)
                ?: false
        ) {
            Text("You are currently signed in with Google.\nEmail: ${uiState.userProfile.email}")
        } else if (uiState.userProfile?.authProviders?.contains(EmailAuthProvider.PROVIDER_ID)
                ?: false
        ) {
            Text("You are currently logged in with email.\nEmail: ${uiState.userProfile.email}")
            Button(
                onClick = {
                    navigateToChangeEmail()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Change email",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            Button(
                onClick = {
                    navigateToChangePassword()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Change password",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }

        if (!(uiState.userProfile?.authProviders?.contains(EmailAuthProvider.PROVIDER_ID)
                ?: false)
        ) {
            Button(
                onClick = {
                    navigateToLinkEmail()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Link email",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
        if (!(uiState.userProfile?.authProviders?.contains(GoogleAuthProvider.PROVIDER_ID)
                ?: false)
        ) {
            ContinueWithGoogleButton("Link Google", isCenteredText = false) { credential ->
                onLinkGoogle(credential)
            }
        }

        if (uiState.userProfile?.isAnonymous == false) {
            Button(
                onClick = {
                    onDeleteAccountClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Delete",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }


        Button(
            onClick = {
                onSignOutClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Sign out",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        HorizontalDivider(thickness = 2.dp)
        Text("Appearance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark theme")

            Switch(
                checked = uiState.isDarkTheme,
                onCheckedChange = {
                    onThemeChange(it)
                }
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true,
)
fun SettingsScreenPreview() {
    SettingsScreen(
        uiState = SettingsUiState(
            userProfile = User(),
            isDarkTheme = false,
            isLoading = false,
            errorMessage = null,
        ),
        onDeleteAccountClick = {},
        onSignOutClick = {},
        navigateToLinkEmail = {},
        onThemeChange = {},
        navigateToChangePassword = {},
        navigateToChangeEmail = {},
        onShowSnackbar = {},
        onErrorMessageShown = {},
        onLinkGoogle = {},
        navigateToSignUp = {}
    )
}