package com.example.donemate.ui.screens.sign_in

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.domain.remote.AccountRepository
import com.example.donemate.data.remote.AuthResult
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
class SignInViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel()  {
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.NotStarted)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    val hasUser: Flow<Boolean> = accountRepository.hasUser.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )


    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }
    fun onSignInClick(){
        viewModelScope.launch {
            _authState.value = accountRepository.authenticate(_uiState.value.email, _uiState.value.password)
        }
    }
    fun onSignInWithGoogle(credential: Credential) {
        viewModelScope.launch {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountRepository.signInWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Log.e("ERROR_TAG", "Er")
            }
        }
    }

    fun onContinueAnonymously() {
        viewModelScope.launch {
            accountRepository.createAnonymousAccount()
        }
    }
}

data class SignInUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
)