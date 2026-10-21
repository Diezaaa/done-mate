package com.example.donemate.ui.common

sealed interface FormState {
    data object Idle : FormState
    data object Submitting : FormState
    data object Success : FormState
    data class Error(val message: String) : FormState
}