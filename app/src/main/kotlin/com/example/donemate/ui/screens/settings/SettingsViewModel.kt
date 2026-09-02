package com.example.donemate.ui.screens.settings

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.model.User
import com.example.donemate.data.repository.local.PreferencesRepository
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.data.repository.remote.AuthResult
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val tasksRepository: TasksRepository,
    private val preferencesRepository: PreferencesRepository,
    ) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> =
        combine(
            preferencesRepository.isDarkTheme,
            accountRepository.userProfile,
            _errorMessage
        ) { isDarkTheme, userProfile, errorMessage ->
            SettingsUiState.Success(
                userProfile = userProfile,
                isDarkTheme = isDarkTheme,
                errorMessage = errorMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading
        )


    fun changeTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            preferencesRepository.changeTheme(isDarkTheme)
        }
    }

    // The function below gets data (credential) from the UI (screen). This violates SSOT\UDF
    fun linkGoogle(credential: Credential) {
        viewModelScope.launch {
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val result = accountRepository.linkGoogle(googleIdTokenCredential.idToken)
                if (result is AuthResult.Error) {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountRepository.signOut()
            tasksRepository.deleteAll()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = accountRepository.deleteAccount()
            if (result is AuthResult.Error) {
                _errorMessage.value = result.message
            }
            else {
                tasksRepository.deleteAll()
            }
        }
    }

    fun errorMessageShown() {
        _errorMessage.value = null
    }

    fun unlinkEmail() {
        viewModelScope.launch {
            val result = accountRepository.unlinkEmail()
            if (result is AuthResult.Error) {
                _errorMessage.value = result.message
            }
        }
    }
}

sealed interface SettingsUiState {
    data class Success(
        val userProfile: User?,
        val isDarkTheme: Boolean,
        val errorMessage: String?
    ) : SettingsUiState
    data object Loading : SettingsUiState
}
