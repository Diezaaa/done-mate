package com.example.donemate.ui.screens.link_email

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

@HiltViewModel
class LinkEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LinkEmailUiState())
    val uiState: StateFlow<LinkEmailUiState> = _uiState.asStateFlow()

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

    fun linkEmail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSuccess = accountRepository.linkEmail(
                        email = _uiState.value.email,
                        password = _uiState.value.password
                    ) is AuthResult.Success
                )
            }
        }
    }

    fun errorMessageShown(){
        _uiState.update {
            it.copy(
                isSuccess = null
            )
        }
    }
}

data class LinkEmailUiState(
    val email: String = "",
    val password: String = "",
    val isSuccess: Boolean? = null,
)