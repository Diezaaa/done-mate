package com.example.donemate.ui.screens.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            when (val result = accountRepository.sendPasswordResetEmail(_uiState.value.email)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isSent = true)
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }
}

data class ResetPasswordUiState(
    val email: String = "",
    val isSent: Boolean = false,
    val errorMessage: String? = null
)