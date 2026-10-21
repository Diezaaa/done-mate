package com.example.donemate.ui.screens.sign_in

import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.example.donemate.domain.ContinueWithGoogleUseCase
import com.example.donemate.ui.common.AuthState
import com.example.donemate.ui.common.AuthUiState
import com.example.donemate.ui.common.convertIsVerifiedToAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val continueWithGoogleUseCase: ContinueWithGoogleUseCase,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun signInWithEmailAndPassword() {
        _uiState.update {
            it.copy(authState = AuthState.Authenticating)
        }
        viewModelScope.launch {
            val result = accountRepository.signInWithEmailAndPassword(
                _uiState.value.email,
                _uiState.value.password
            )
            when (result) {
                is AuthResult.Success -> {
                    val isUserVerified = accountRepository.isUserVerified.first()
                    _uiState.update {
                        it.copy(
                            authState =
                                convertIsVerifiedToAuthState(isUserVerified)
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(authState = AuthState.Error(result.message))
                    }
                }
            }
        }
    }

    // TODO: The function below gets data (credential) from the UI (screen). This violates SSOT\UDF. Consider fixing it.
    fun continueWithGoogle(credential: Credential) {
        _uiState.update {
            it.copy(authState = AuthState.Authenticating)
        }
        viewModelScope.launch {
            when (val result = continueWithGoogleUseCase(credential)) {
                is AuthResult.Success -> {
                    val isUserVerified = accountRepository.isUserVerified.first()
                    _uiState.update {
                        it.copy(authState = convertIsVerifiedToAuthState(isUserVerified))
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(authState = AuthState.Error(result.message))
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(authState = AuthState.Idle)
        }
    }
}
