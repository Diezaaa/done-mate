package com.example.donemate.ui.screens.link_email

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LinkAccountUiState())
    val uiState: StateFlow<LinkAccountUiState> = _uiState.asStateFlow()

    fun updateEmail(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun updatePassword(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun linkEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSuccess = accountRepository.linkEmail(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                ) is AuthResult.Success
            )
        }
    }

    fun errorMessageShown(){
        _uiState.value = _uiState.value.copy(
            isSuccess = null
        )
    }
}

data class LinkAccountUiState(
    val email: String = "",
    val password: String = "",
    val isSuccess: Boolean? = null,
)