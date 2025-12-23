package com.example.donemate.ui.screens.link_account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.model.User
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.impl.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkAccountViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel()  {
    private val _authState = MutableStateFlow<AuthResult>(AuthResult.NotStarted)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()
    private val _uiState = MutableStateFlow(LinkAccountUiState())
    val uiState: StateFlow<LinkAccountUiState> = _uiState.asStateFlow()

    val currentUser: Flow<User?> = accountService.currentUser.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )


    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onLinkAccount(currentUser: User?) {


        viewModelScope.launch {
            _authState.value = accountService.linkAccount(_uiState.value.email, _uiState.value.password)
        }

    }
}

data class LinkAccountUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
)