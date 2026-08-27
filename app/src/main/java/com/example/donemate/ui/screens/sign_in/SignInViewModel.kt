package com.example.donemate.ui.screens.sign_in

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.example.donemate.ui.common.AuthForm
import com.example.donemate.ui.common.AuthUiState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _authForm = MutableStateFlow(AuthForm())

    private val _isUserVerified = MutableStateFlow<Boolean?>(null)


    val uiState: StateFlow<AuthUiState> =
        combine(_authForm, _isUserVerified) { authForm, isUserVerified ->
            AuthUiState(
                authForm = authForm,
                isUserVerified = isUserVerified
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthUiState()
        )




    fun updateEmail(email: String) {
        _authForm.value = _authForm.value.copy(email = email)
    }

    fun updatePassword(newValue: String) {
        _authForm.value = _authForm.value.copy(password = newValue)
    }

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            val result = accountRepository.signInWithEmailAndPassword(
                _authForm.value.email,
                _authForm.value.password
            )
            when (result) {
                is AuthResult.Success -> {
                    _isUserVerified.value = accountRepository.isUserVerified.first()
                }
                is AuthResult.Error -> {
                    _authForm.value = _authForm.value.copy(errorMessage = result.message)
                }
            }
        }
    }

    // The function below gets data (credential) from the UI (screen). This violates SSOT\UDF
    fun continueWithGoogle(credential: Credential) {
        viewModelScope.launch {
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val result = accountRepository.continueWithGoogle(googleIdTokenCredential.idToken)
                when (result) {
                    is AuthResult.Success -> {
                        _isUserVerified.value = accountRepository.isUserVerified.first()
                    }
                    is AuthResult.Error -> {
                        _authForm.value = _authForm.value.copy(errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _authForm.value = _authForm.value.copy(
            errorMessage = null
        )
    }
}
