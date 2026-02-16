package com.example.donemate.ui.link_account.fake.remote

import com.example.donemate.data.remote.AuthResult
import com.example.donemate.data.remote.models.User
import com.example.donemate.domain.remote.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AccountRepositoryFake() : AccountRepository {

    private val _currentUser = MutableStateFlow(null)

    // ---- Properties used by ViewModels ----
    override val currentUser: Flow<User?> = _currentUser

    override val hasUser: Flow<Boolean> = _currentUser.map { it != null }

    override val currentUserId: String
        get() = "323"


    override suspend fun linkAccount(email: String, password: String): AuthResult {
        return AuthResult.Success
    }

    override suspend fun authenticate(email: String, password: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun createAnonymousAccount() {
        error("Not needed for these tests")
    }

    override suspend fun deleteAccount(): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun resetPassword(email: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun createAccount(email: String, password: String): AuthResult {
        error("Not needed for these tests")
    }

    override suspend fun signInWithGoogle(idToken: String) {
        error("Not needed for these tests")
    }
}
