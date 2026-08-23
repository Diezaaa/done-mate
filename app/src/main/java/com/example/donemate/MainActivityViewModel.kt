package com.example.donemate

import android.util.Log
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val isOfflineDialogShownOnce = MutableStateFlow(false)

    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesRepository.isDarkTheme,
        accountRepository.userProfile,
        isOfflineDialogShownOnce
    ) { isDarkTheme, userProfile, isOfflineDialogShownOnce ->
        MainActivityUiState.Success(
            userProfile = userProfile,
            isDarkTheme = isDarkTheme,
            isOfflineDialogShownOnce = isOfflineDialogShownOnce
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState.Loading
    )

    fun onOfflineDialogDismiss() {
        isOfflineDialogShownOnce.value = true
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        override val userProfile: User?,
        override val isDarkTheme: Boolean,
        override val isOfflineDialogShownOnce: Boolean
    ) : MainActivityUiState

    val userProfile: User? get() = null
    val isDarkTheme: Boolean get() = false
    val isOfflineDialogShownOnce: Boolean get() = false

    fun shouldKeepSplashScreen(): Boolean { return this is Loading }
}