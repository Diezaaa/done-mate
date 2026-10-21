package com.example.donemate.ui.common

fun convertIsVerifiedToAuthState(isUserVerified: Boolean?): AuthState {
    return when (isUserVerified) {
        true -> AuthState.Verified
        false -> AuthState.Verify
        else -> AuthState.Idle
    }
}