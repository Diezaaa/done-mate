package com.example.donemate.ui.screens.update_email

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

data class UpdateEmailUiState(
    val email: String = "",
    val formState: FormState = FormState.Idle
)

@HiltViewModel
class UpdateEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UpdateEmailUiState())

    val uiState: StateFlow<UpdateEmailUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun updateUserEmail() {
        _uiState.update {
            it.copy(formState = FormState.Submitting)
        }
        viewModelScope.launch {
            when (val result = accountRepository.updateEmail(email = _uiState.value.email)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(formState = FormState.Success)
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(formState = FormState.Error(result.message))
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