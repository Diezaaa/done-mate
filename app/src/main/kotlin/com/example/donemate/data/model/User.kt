package com.example.donemate.data.model

data class User(
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val authProviders: List<String> = emptyList(),
    val googleEmail: String? = null
)

