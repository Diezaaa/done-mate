package com.example.donemate.ui.screens.sign_in

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.example.donemate.ui.common.AuthUiState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    // Converting result using if\else is very repetitive. Consider adding a function
    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            val result = accountRepository.signInWithEmailAndPassword(
                    _uiState.value.email,
                    _uiState.value.password
                )
            if (result is AuthResult.Error) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
            }
            else {
                _uiState.value = _uiState.value.copy(
                    isSuccess = true
                )
            }
        }
    }

    fun continueWithGoogle(credential: Credential) {
        viewModelScope.launch {
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val result = accountRepository.continueWithGoogle(googleIdTokenCredential.idToken)
                if (result is AuthResult.Error) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message
                    )
                }
                else {
                    _uiState.value = _uiState.value.copy(
                        isSuccess = true
                    )
                }
            }
        }
    }

    fun errorMessageShown(){
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
