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

private const val TAG = "DefaultAccountRepository"

class DefaultAccountRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AccountRepository {

    override fun getUserId(): String? = firebaseAuth.currentUser?.uid

    // TODO: Consider combining isUserVerified into userProfile
    override val userProfile: Flow<User?> =
        firebaseUser().map { firebaseUser ->
            if (firebaseUser == null)
                null
            else {
                User(
                    email = firebaseUser.email,
                    googleEmail = firebaseUser.providerData.find { it.providerId == GoogleAuthProvider.PROVIDER_ID }?.email,
                    isEmailVerified = firebaseUser.isEmailVerified,
                    authProviders = firebaseUser.providerData.map { it.providerId }
                )
            }
        }

    /** User is verified when he has verified his email or/and is linked with Google */
    override val isUserVerified: Flow<Boolean?> =
        userProfile.map { it ->
            if (it == null) return@map null
            else {
                it.isEmailVerified || firebaseAuth.currentUser?.providerData?.any {
                    it.providerId == GoogleAuthProvider.PROVIDER_ID
                } == true
            }
        }

    override val isEmailVerified: Flow<Boolean?> =
        userProfile.map {
            if (it == null) return@map null
            it.isEmailVerified
        }


    // Learn more about flows and REFACTOR this function
    private fun firebaseUser(): Flow<FirebaseUser?> = channelFlow {
        send(firebaseAuth.currentUser)
        while (true) {
            reloadFirebaseUser(this)
            delay(1.seconds)
        }
    }

    // TODO: Learn more about flows and REFACTOR this function
    private suspend fun reloadFirebaseUser(scope: ProducerScope<FirebaseUser?>) {
        try {
            firebaseAuth.currentUser!!.reload().await()
            scope.send(firebaseAuth.currentUser)
        } catch (_: Exception) {
            scope.send(firebaseAuth.currentUser)
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): AuthResult {
        return try {
            val firebaseUser = firebaseUser().first()!!
            val credential =
                EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, oldPassword)
            firebaseUser.reauthenticate(credential).await()
            firebaseUser.updatePassword(newPassword).await()
            val newCredential =
                EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, newPassword)
            firebaseUser.reauthenticate(newCredential).await()
            AuthResult.Success
        } catch (e: Exception) {
            Log.d("updatePass", e.toString())
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun updateEmail(email: String): AuthResult {
        return try {
            firebaseUser().first()!!.verifyBeforeUpdateEmail(email).await()
            return AuthResult.Success
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            AuthResult.Error("Re-login and then try again")
        } catch (e: Exception) {
            Log.e(TAG, "updateEmail: ${e.message}")
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Invalid email or password")
        } catch (e: Exception) {
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
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            firebaseAuth.currentUser!!.sendEmailVerification().await()
            AuthResult.Success
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            AuthResult.Error("Re-login and then try again")
        } catch (e: Exception) {
            firebaseAuth.currentUser?.unlink(EmailAuthProvider.PROVIDER_ID)
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
        } catch (_: Exception) {
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
            firebaseAuth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
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
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseUser().first()!!.sendEmailVerification().await()
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }


    override suspend fun continueWithGoogle(idToken: String): AuthResult {
        return try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(firebaseCredential).await()
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

    override suspend fun unlinkEmail(): AuthResult {
        return try {
            firebaseUser().first()!!.unlink(EmailAuthProvider.PROVIDER_ID)
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.Error("Something went wrong")
        }
    }
}
