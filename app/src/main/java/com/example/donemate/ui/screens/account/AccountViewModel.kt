package com.example.donemate.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.model.Task
import com.example.donemate.model.User
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.StorageService
import com.example.donemate.ui.screens.sign_in.SignInUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel()  {
    val currentEmail: StateFlow<String?> = accountService.currentUser
        .map { user -> user?.email }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    val currentUser: StateFlow<User?> = accountService.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    private val _hasUser: StateFlow<Boolean> = accountService.hasUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        true
    )

    val hasUser: StateFlow<Boolean> = _hasUser

    fun onLogOut(){
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    fun onDeleteAccount(){
        viewModelScope.launch {
            accountService.deleteAccount()
        }
    }
}
