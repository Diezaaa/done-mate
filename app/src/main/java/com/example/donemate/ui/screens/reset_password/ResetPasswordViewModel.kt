package com.example.donemate.ui.screens.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(email: String){
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun sendPasswordResetEmail(){
        viewModelScope.launch {
           accountRepository.sendPasswordResetEmail(_uiState.value.email)
            _uiState.value = _uiState.value.copy(isSent = true)
        }
    }
}

data class ResetPasswordUiState(
    val email: String = "",
    val isSent: Boolean = false
)