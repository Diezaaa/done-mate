package com.example.donemate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.model.User
import com.example.donemate.data.repository.local.PreferencesRepository
import com.example.donemate.data.repository.remote.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository,
    accountRepository: AccountRepository
) : ViewModel() {
    private val wasOfflineDialogShown = MutableStateFlow(false)

    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesRepository.isDarkTheme,
        accountRepository.userProfile,
        wasOfflineDialogShown,
        accountRepository.isUserVerified
    ) { isDarkTheme, userProfile, wasOfflineDialogShown, isUserVerified ->
        MainActivityUiState.Success(
            userProfile = userProfile,
            isDarkTheme = isDarkTheme,
            wasOfflineDialogShown = wasOfflineDialogShown,
            isUserVerified = isUserVerified
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState.Loading
    )

    fun dismissOfflineDialog() {
        wasOfflineDialogShown.value = true
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val userProfile: User?,
        val isDarkTheme: Boolean,
        val wasOfflineDialogShown: Boolean,
        val isUserVerified: Boolean?
    ) : MainActivityUiState

    fun shouldKeepSplashScreen(): Boolean {
        return this is Loading
    }
}