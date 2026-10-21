package com.example.donemate.ui.screens.update_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.example.donemate.ui.common.FormState
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
    val formState: FormState = FormState.Idle
)
@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UpdatePasswordUiState())


    val uiState: StateFlow<UpdatePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.update {
            it.copy(currentPassword = password)
        }
    }

    fun updateNewPassword(password: String) {
        _uiState.update {
            it.copy(newPassword = password)
        }
    }

    fun updatePassword() {
        _uiState.update {
            it.copy(formState = FormState.Submitting)
        }
        viewModelScope.launch {
            val result = accountRepository.updatePassword(
                oldPassword = _uiState.value.currentPassword,
                newPassword = _uiState.value.newPassword
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            formState = FormState.Success
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            formState = FormState.Error(result.message)
                        )
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update {
            it.copy(
                formState = FormState.Idle
            )
        }
    }
}