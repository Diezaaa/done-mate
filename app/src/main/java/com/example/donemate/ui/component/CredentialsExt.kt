package com.example.donemate.ui.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.donemate.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import kotlinx.coroutines.launch


@Composable
fun ContinueWithGoogleButton(
    buttonText: String,
    isCenteredText: Boolean = true,
    onRequestResult: (Credential) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                launchCredManButtonUI(
                    context,
                    onRequestResult
                )
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F)
        ),
        border = BorderStroke(1.dp, Color(0xFF747775)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = if (isCenteredText) Arrangement.Center else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = buttonText,
                modifier = Modifier.size(24.dp)

            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = buttonText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

private suspend fun launchCredManButtonUI(
    context: Context,
    onRequestResult: (Credential) -> Unit
) {
    try {
        val signInWithGoogleOption = GetSignInWithGoogleOption
            .Builder(serverClientId = context.getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val result = CredentialManager.create(context).getCredential(
            request = request,
            context = context
        )

        onRequestResult(result.credential)
    } catch (e: NoCredentialException) {
        Log.d("ERROR_TAG", e.message.orEmpty())
    } catch (e: GetCredentialException) {
        Log.d("ERROR_TAG", e.message.orEmpty())
    }
}

suspend fun launchContinueWithGoogleBottomSheet(
    context: Context,
    hasFilter: Boolean = true,
    onRequestResult: (Credential) -> Unit
) {
    try {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(hasFilter)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = CredentialManager.create(context).getCredential(
            request = request,
            context = context
        )

        onRequestResult(result.credential)
    } catch (e: NoCredentialException) {
        Log.d("ERROR_TAG", e.message.orEmpty())

        //If the bottom sheet was launched with filter by authorized accounts, we launch it again
        //without filter so the user can see all available accounts, not only the ones that have
        //been previously authorized in this app
        if (hasFilter) {
            launchContinueWithGoogleBottomSheet(context, hasFilter = false, onRequestResult)
        }
    } catch (e: GetCredentialException) {
        Log.d("ERROR_TAG", e.message.orEmpty())
    }
}