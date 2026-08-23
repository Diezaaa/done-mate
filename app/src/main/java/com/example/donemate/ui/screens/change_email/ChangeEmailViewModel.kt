package com.example.donemate.ui.screens.change_email

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

data class ChangeEmailUiState(
    val email: String = "",
    val password: String = "",
    val isFailure: Boolean? = null,
    val errorMessage: String? = null,
    val shouldRedirectToVerification: Boolean = false
)

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ChangeEmailUiState()
    )
    val uiState: StateFlow<ChangeEmailUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun changeEmail() {
        viewModelScope.launch {
            val result = accountRepository.changeEmail(email = _uiState.value.email)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isFailure = false)
                    _uiState.value = _uiState.value.copy(shouldRedirectToVerification = true)

                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(isFailure = true)
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)

                }
            }

        }
    }

    fun changeShouldRedirectToVerification(shouldRedirectToVerification: Boolean){
        _uiState.value = _uiState.value.copy(
            shouldRedirectToVerification = shouldRedirectToVerification
        )
    }

    fun errorMessageShown() {
        _uiState.value = _uiState.value.copy(
            isFailure = null
        )
    }
}