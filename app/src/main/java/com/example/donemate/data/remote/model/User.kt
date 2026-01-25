package com.example.donemate.data.remote.models

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val email: String? = ""
)