package com.example.donemate.ui.link_account.fake.remote

import com.example.donemate.data.repository.remote.AuthResult
import com.example.donemate.data.model.User
import com.example.donemate.data.repository.remote.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AccountRepositoryFake() : AccountRepository {

    private val _currentUser = MutableStateFlow(null)

    // ---- Properties used by ViewModels ----
    override val currentUser: Flow<User?> = _currentUser

    override val isLoggedIn: Flow<Boolean> = _currentUser.map { it != null }

    override val currentUserId: String
        get() = "323"


    override suspend fun linkEmail(email: String, password: String): AuthResult {
        return AuthResult.Success
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun signInAnonymously(): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun deleteAccount(): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun continueWithGoogle(idToken: String): AuthResult {
        error("Not needed for these tests")
    }
}
