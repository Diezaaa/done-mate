package com.example.donemate.ui.common

data class AuthUiState (
    val authForm: AuthForm = AuthForm(),
    val isUserVerified: Boolean? = null,
)