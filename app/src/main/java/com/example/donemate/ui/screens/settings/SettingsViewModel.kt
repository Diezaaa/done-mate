package com.example.donemate.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.local.ThemeRepositoryImpl
import com.example.donemate.data.remote.models.User
import com.example.donemate.domain.remote.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val themeRepositoryImpl: ThemeRepositoryImpl
) : ViewModel()  {
    val currentEmail: StateFlow<String?> = accountRepository.currentUser
        .map { user -> user?.email }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    val currentUser: StateFlow<User?> = accountRepository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    private val _hasUser: StateFlow<Boolean> = accountRepository.hasUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        true
    )

    val hasUser: StateFlow<Boolean> = _hasUser

    private var _isDarkTheme: StateFlow<Boolean> = themeRepositoryImpl.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        true
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme


    fun onChangeTheme(){
        viewModelScope.launch {
            themeRepositoryImpl.changeTheme(!isDarkTheme.value)
        }
    }
    fun onLogOut(){
        viewModelScope.launch {
            accountRepository.signOut()
        }
    }

    fun onDeleteAccount(){
        viewModelScope.launch {
            accountRepository.deleteAccount()
        }
    }
}
