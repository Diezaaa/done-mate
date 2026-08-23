package com.example.donemate.ui.screens.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ChangePasswordUiState()
    )
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.value = _uiState.value.copy(currentPassword = password)
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
    }

    fun changePassword() {
        viewModelScope.launch {
            val result = accountRepository.updatePassword(
                oldPassword = _uiState.value.currentPassword,
                newPassword = _uiState.value.newPassword
            )
            if (result is AuthResult.Error) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.message
                )
            }
        }
    }

    fun errorMessageShown(){
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}