package com.example.donemate.ui.common

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val authState: AuthState = AuthState.Idle
)
