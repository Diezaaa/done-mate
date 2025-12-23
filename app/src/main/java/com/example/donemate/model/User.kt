package com.example.donemate.model 

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val email: String? = ""
)