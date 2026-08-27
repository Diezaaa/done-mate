package com.example.donemate.ui.common

data class AuthForm(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
)

