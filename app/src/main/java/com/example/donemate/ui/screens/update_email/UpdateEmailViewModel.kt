package com.example.donemate.ui.screens.update_email

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

data class UpdateEmailUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean? = null
)

@HiltViewModel
class UpdateEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UpdateEmailUiState())

    val uiState: StateFlow<UpdateEmailUiState> = _uiState.asStateFlow()


    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updateUserEmail() {
        viewModelScope.launch {
            when (val result = accountRepository.updateEmail(email = _uiState.value.email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSuccess = true)
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)
                }
            }
        }
    }


    fun errorMessageShown() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}