package com.example.donemate.data.repository.remote

import com.example.donemate.data.model.User
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val userProfile: Flow<User?>
    fun getUserId(): String?
    suspend fun updatePassword(oldPassword: String, newPassword: String): AuthResult
    suspend fun changeEmail(email: String): AuthResult
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun linkEmail(email: String, password: String): AuthResult
    suspend fun deleteAccount(): AuthResult
    suspend fun signOut(): AuthResult
    suspend fun unlinkAuthProvidersExceptEmail(): AuthResult
    suspend fun sendPasswordResetEmail(email: String): AuthResult
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun continueWithGoogle(idToken: String): AuthResult
    suspend fun linkGoogle(idToken: String): AuthResult
}