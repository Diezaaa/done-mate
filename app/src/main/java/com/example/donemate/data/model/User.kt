package com.example.donemate.data.model

data class User(
    val email: String? = "",
    val isVerified: Boolean = false,
    val isAnonymous: Boolean = false,
    val authProviders : List<String> = emptyList()
)

