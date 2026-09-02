package com.example.donemate.ui.screens.update_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdatePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean? = null
)

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        UpdatePasswordUiState()
    )
    val uiState: StateFlow<UpdatePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(currentPassword = password)
        }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(newPassword = password)
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            val result = accountRepository.updatePassword(
                oldPassword = _uiState.value.currentPassword,
                newPassword = _uiState.value.newPassword
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isSuccess = true
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }
}