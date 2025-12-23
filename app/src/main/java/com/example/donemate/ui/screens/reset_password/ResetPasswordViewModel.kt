package com.example.donemate.ui.screens.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.impl.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String){
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onForgotPassword(){
        viewModelScope.launch {
            if (accountService.resetPassword(_uiState.value.email) is AuthResult.Success){
                _uiState.value = _uiState.value.copy(isSuccess = true)
            }
        }
    }
}

data class ResetPasswordUiState(
    val email: String = "",
    val isSuccess: Boolean = false
)