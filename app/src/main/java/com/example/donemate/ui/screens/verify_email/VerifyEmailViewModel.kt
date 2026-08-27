package com.example.donemate.ui.screens.verify_email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.model.User
import com.example.donemate.data.repository.remote.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val user: StateFlow<VerifyEmailUiState> = accountRepository.userProfile.map {
        VerifyEmailUiState.Success(it)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VerifyEmailUiState.Loading
        )

    fun unlinkAuthProvidersExceptEmail() {
        viewModelScope.launch {
            accountRepository.unlinkAuthProvidersExceptEmail()
        }
    }

}

sealed interface VerifyEmailUiState {
    data object Loading : VerifyEmailUiState
    data class Success(val user: User?) : VerifyEmailUiState
}