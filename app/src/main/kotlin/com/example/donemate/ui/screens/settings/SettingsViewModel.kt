package com.example.donemate.ui.screens.settings

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.model.User
import com.example.donemate.data.repository.local.PreferencesRepository
import com.example.donemate.data.repository.offline_first.QuotesRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val tasksRepository: TasksRepository,
    private val preferencesRepository: PreferencesRepository,
    private val quotesRepository: QuotesRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    private var _shouldRedirectToSignUp = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> =
        combine(
            preferencesRepository.isDarkTheme,
            accountRepository.userProfile,
            _errorMessage,
            _shouldRedirectToSignUp
        ) { isDarkTheme, userProfile, errorMessage, shouldRedirectToSignUp ->
            SettingsUiState.Success(
                userProfile = userProfile,
                isDarkTheme = isDarkTheme,
                errorMessage = errorMessage,
                shouldRedirectToSignUp = shouldRedirectToSignUp
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

    // TODO: The function below gets data (credential) from the UI (screen). This violates SSOT\UDF. Consider fixing it.
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
            val result = accountRepository.signOut()
            if (result is AuthResult.Error){
                _errorMessage.value = result.message
            }
            else {
                tasksRepository.deleteAll()
                quotesRepository.clearAll()
                _shouldRedirectToSignUp.value = true
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = accountRepository.deleteAccount()
            if (result is AuthResult.Error) {
                _errorMessage.value = result.message
            } else {
                tasksRepository.deleteAll()
                quotesRepository.clearAll()
                _shouldRedirectToSignUp.value = true
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

// TODO: Consider adding Error state. Determine if the repositories can return errors
sealed interface SettingsUiState {
    data class Success(
        val userProfile: User?,
        val isDarkTheme: Boolean,
        val errorMessage: String?,
        val shouldRedirectToSignUp: Boolean
    ) : SettingsUiState

    data object Loading : SettingsUiState
}
