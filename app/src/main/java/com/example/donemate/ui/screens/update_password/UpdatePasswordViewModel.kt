package com.example.donemate.ui.screens.update_password

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
        _uiState.value = _uiState.value.copy(currentPassword = password)
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
    }

    fun updatePassword() {
        viewModelScope.launch {
            val result = accountRepository.updatePassword(
                oldPassword = _uiState.value.currentPassword,
                newPassword = _uiState.value.newPassword
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message
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