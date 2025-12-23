package com.example.donemate.ui.screens.account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

@Composable
fun AccountScreen(
    navigateToSignIn: () -> Unit,
    vm: AccountViewModel,
    navigateToLinkAccount: () -> Boolean
) {
    val email by vm.currentEmail.collectAsStateWithLifecycle()
    val currentUser by vm.currentUser.collectAsStateWithLifecycle()
    val hasUser by vm.hasUser.collectAsStateWithLifecycle()

    LaunchedEffect(hasUser) {
        if (!hasUser){
            navigateToSignIn()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        currentUser?.isAnonymous?.let {
            if(!it) {
                Text("Your e-mail: $email")
                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = {
                        vm.onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(200.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete account"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete account")
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        currentUser?.isAnonymous?.let {
            if (it) {
                Text("You are currently in anonymous account.")
            }
        }
        Button(
            onClick = {
                vm.onLogOut()
                navigateToSignIn()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Log out"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log out")
        }
        currentUser?.isAnonymous?.let {
            if (it) {
                Button(
                    onClick = {
                        navigateToLinkAccount()
                    },
                    modifier = Modifier.width(200.dp))
                {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Log out"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Link e-mail")

                }
            }
        }
    }
}