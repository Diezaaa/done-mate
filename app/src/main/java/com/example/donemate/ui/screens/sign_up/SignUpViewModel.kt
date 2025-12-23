package com.example.donemate.ui.screens.sign_up

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.R
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.impl.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel()  {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.NotStarted)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()

    val hasUser: Flow<Boolean> = accountService.hasUser.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )


    fun onSignUpWithGoogle(credential: Credential) {
        viewModelScope.launch {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Log.e("ERROR_TAG", "Er")
            }
        }
    }
    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }
    fun onSignUpClick() {
        viewModelScope.launch {
            _authState.value = accountService.createAccount(_uiState.value.email, _uiState.value.password)
        }
    }

    fun onContinueAnonymously() {
        viewModelScope.launch {
            accountService.createAnonymousAccount()
        }
    }
}

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = ""
)