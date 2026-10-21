package com.example.donemate.ui.common

sealed interface AuthState {
    data object Idle : AuthState
    data object Authenticating : AuthState
    data object Verify : AuthState
    data object Verified : AuthState
    data class Error(val message: String) : AuthState
}