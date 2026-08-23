package com.example.donemate.data.repository.remote

import android.util.Log
import com.example.donemate.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.time.Duration.Companion.seconds

// TODO: Remove this code and make use of mine Result instead of AuthResult. Maybe I should use Kotlin's Result
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class DefaultAccountRepository @Inject constructor(private val auth: FirebaseAuth) :
    AccountRepository {

    override fun getUserId(): String? = auth.currentUser?.uid

    override val userProfile: Flow<User?> =
        firebaseUser().map { firebaseUser ->
            if (firebaseUser == null)
                null
            else {
                val email =
                    firebaseUser.providerData.find {
                        it.providerId == GoogleAuthProvider.PROVIDER_ID
                    }?.email ?: firebaseUser.providerData.find {
                        it.providerId == EmailAuthProvider.PROVIDER_ID
                    }?.email
                User(
                    email = email,
                    isAnonymous = firebaseUser.isAnonymous,
                    isVerified = firebaseUser.isEmailVerified,
                    authProviders = firebaseUser.providerData.map { it.providerId }
                )
            }
        }

    // Learn more about flows and REFACTOR this function
    fun firebaseUser(): Flow<FirebaseUser?> = channelFlow {
        while (true) {
            reloadFirebaseUser(this)
            delay(1.seconds)
        }
    }

    // Learn more about flows and refactor this function
    private suspend fun reloadFirebaseUser(scope: ProducerScope<FirebaseUser?>) {
        try {
            auth.currentUser!!.reload().await()
            scope.send(auth.currentUser)
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
            scope.send(null)
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): AuthResult {
        return try {
            val firebaseUser = firebaseUser().first()!!
            val credential =
                EmailAuthProvider.getCredential(auth.currentUser!!.email!!, oldPassword)
            firebaseUser.reauthenticate(credential).await()
            firebaseUser.updatePassword(newPassword).await()
            AuthResult.Success
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Your current password is incorrect")
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun changeEmail(email: String): AuthResult {
        return try {
            firebaseUser().first()!!.verifyBeforeUpdateEmail(email).await()
            return AuthResult.Success
        } catch (firebaseAuthRecentLoginRequiredException: FirebaseAuthRecentLoginRequiredException) {
            AuthResult.Error("Re-login and then try again")
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Invalid email or password")
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun linkEmail(email: String, password: String): AuthResult {
        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            if (firebaseUser().first()!!.providerData.map { it.providerId }
                    .contains(EmailAuthProvider.PROVIDER_ID)) {
                firebaseUser().first()!!.unlink(EmailAuthProvider.PROVIDER_ID).await()
            }
            firebaseUser().first()!!.linkWithCredential(credential).await()
            auth.signInWithEmailAndPassword(email, password).await()
            auth.currentUser!!.sendEmailVerification().await()
            AuthResult.Success
        } catch (e: Exception) {
            auth.currentUser?.unlink(EmailAuthProvider.PROVIDER_ID)

            Log.d("TAG", e.toString())
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun linkGoogle(idToken: String): AuthResult {
        return try {
            val firebaseUser = firebaseUser().first()!!
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseUser.linkWithCredential(credential).await()
            if (firebaseUser.providerData.map { it.providerId }
                    .contains(EmailAuthProvider.PROVIDER_ID)) {
                firebaseUser.unlink(EmailAuthProvider.PROVIDER_ID).await()
            }
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }


    override suspend fun deleteAccount(): AuthResult {
        return try {
            firebaseUser().first()!!.delete().await()
            AuthResult.Success
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            AuthResult.Error("Re-login and then try again")
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun signOut(): AuthResult {
        return try {
            auth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            firebaseUser().first()!!.sendEmailVerification()
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }


    override suspend fun continueWithGoogle(idToken: String): AuthResult {
        return try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential).await()
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun unlinkAuthProvidersExceptEmail(): AuthResult {
        return try {
            firebaseUser().first()!!.providerData.filter { it.providerId != EmailAuthProvider.PROVIDER_ID && it.providerId != FirebaseAuthProvider.PROVIDER_ID }
                .forEach { provider -> firebaseUser().first()!!.unlink(provider.providerId) }
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }
}
