package com.example.donemate.model.service.impl

import android.util.Log
import androidx.compose.ui.util.trace
import com.example.donemate.model.User
import com.example.donemate.model.service.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider

sealed class AuthResult {
    object NotStarted : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Flow<Boolean>
        get() = currentUser.map { it != null }

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous, it.email) } ?:null)
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun authenticate(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }


    override suspend fun linkAccount(email: String, password: String): AuthResult {

        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser!!.linkWithCredential(credential).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun deleteAccount(): AuthResult {
        return try {
            auth.currentUser!!.delete().await()
            AuthResult.Success
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            AuthResult.Error("Re-login and then try again")
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email)
            AuthResult.Success
        }
        catch (e: Exception){
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun createAccount(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }


    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).await()
    }
}
