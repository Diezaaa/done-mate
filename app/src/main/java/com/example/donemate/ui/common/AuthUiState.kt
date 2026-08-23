package com.example.donemate.ui.common

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

