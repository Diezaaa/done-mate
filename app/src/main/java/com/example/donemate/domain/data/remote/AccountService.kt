package com.example.donemate.domain.data.remote

import com.example.donemate.data.remote.models.User
import com.example.donemate.data.remote.AuthResult
import kotlinx.coroutines.flow.Flow
interface AccountService {
    val currentUserId: String
    val hasUser: Flow<Boolean>
    val currentUser: Flow<User?>
    suspend fun authenticate(email: String, password: String): AuthResult
    suspend fun createAnonymousAccount()
    suspend fun linkAccount(email: String, password: String): AuthResult
    suspend fun deleteAccount(): AuthResult
    suspend fun signOut()
    suspend fun resetPassword(email: String): AuthResult
    suspend fun createAccount(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String)

}