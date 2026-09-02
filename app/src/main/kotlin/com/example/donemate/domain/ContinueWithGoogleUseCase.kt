package com.example.donemate.domain

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

/**
 * A use case which deals with logging in the user using Google
 */
class ContinueWithGoogleUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(credential: Credential): AuthResult {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)
            return accountRepository.continueWithGoogle(googleIdTokenCredential.idToken)
        }
        return AuthResult.Error("Something went wrong.")
    }
}