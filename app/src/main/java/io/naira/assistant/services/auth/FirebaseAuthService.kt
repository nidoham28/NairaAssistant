package io.naira.assistant.services.auth

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Provides email/password authentication through Firebase Auth.
 *
 * Firebase Auth persists the session. Registration signs the new account in immediately;
 * password recovery is completed through Firebase's email action link.
 */
class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    /** Whether Firebase has restored a signed-in user for the current app session. */
    val hasCurrentUser: Boolean
        get() = auth.currentUser != null

    /** Signs in with email/password and returns Firebase failures without logging credentials. */
    suspend fun login(email: String, password: String): Result<Unit> =
        authRequest {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
        }

    /** Creates the account; Firebase also signs the new user in immediately. */
    suspend fun register(email: String, password: String): Result<Unit> =
        authRequest {
            auth.createUserWithEmailAndPassword(email.trim(), password).await()
        }

    /** Requests Firebase's hosted password-reset email for [email]. */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        authRequest {
            auth.sendPasswordResetEmail(email.trim()).await()
        }

    /** Returns a client-side password validation message, or `null` when the password is valid. */
    fun passwordError(password: String): String? =
        FirebaseAuthValidation.passwordError(password)

    /** Converts known Firebase auth errors into user-facing, non-secret messages. */
    fun friendlyMessage(error: Throwable): String {
        val errorCode = (error as? FirebaseAuthException)?.errorCode
        return FirebaseAuthValidation.friendlyMessage(errorCode)
    }

    private suspend fun authRequest(request: suspend () -> Unit): Result<Unit> =
        try {
            request()
            Result.success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: FirebaseException) {
            Result.failure(error)
        }

}

internal object FirebaseAuthValidation {
    private const val MIN_PASSWORD_LENGTH = 8

    fun passwordError(password: String): String? = when {
        password.length < MIN_PASSWORD_LENGTH ->
            "Password must be at least $MIN_PASSWORD_LENGTH characters."
        password.none { it.isLetter() } || password.none { it.isDigit() } ->
            "Password must contain at least one letter and one number."
        else -> null
    }

    fun friendlyMessage(errorCode: String?): String =
        when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Enter a valid email address."
            "ERROR_WRONG_PASSWORD",
            "ERROR_USER_NOT_FOUND",
            "ERROR_INVALID_CREDENTIAL",
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "These account details couldn't be used. Check them or try signing in."
            "ERROR_WEAK_PASSWORD" ->
                "Password must be at least $MIN_PASSWORD_LENGTH characters and contain a letter and a number."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Wait a little and try again."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Couldn't connect to Firebase. Check your internet connection and try again."
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "Email/password sign-in is not enabled for this Firebase project."
            else -> "Authentication failed. Please try again."
        }
}
