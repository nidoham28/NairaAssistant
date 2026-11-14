package io.naira.assistant.services.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Provides email/password and Google authentication through Firebase Auth.
 *
 * Firebase Auth persists the session. Registration signs the new account in immediately;
 * password recovery is completed through Firebase's email action link. Google credentials
 * are retrieved with Credential Manager and exchanged for a Firebase credential without
 * logging or persisting the ID token.
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

    /**
     * Signs in with a Google account selected through Credential Manager.
     *
     * [serverClientId] must be the Firebase project's web OAuth client ID. Cancellation,
     * Credential Manager failures, token parsing failures, and Firebase errors are returned
     * as failures; coroutine cancellation is rethrown.
     */
    suspend fun signInWithGoogle(context: Context, serverClientId: String): Result<Unit> =
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credential = CredentialManager.create(context)
                .getCredential(context, request)
                .credential

            val idToken = if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                return Result.failure(
                    IllegalStateException("Credential Manager returned an unsupported credential type."),
                )
            }

            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            Result.success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: GetCredentialException) {
            Result.failure(error)
        } catch (error: GoogleIdTokenParsingException) {
            Result.failure(error)
        } catch (error: FirebaseException) {
            Result.failure(error)
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

    /** Returns true when the user explicitly dismissed the Credential Manager UI. */
    fun isGoogleSignInCancelled(error: Throwable): Boolean =
        FirebaseAuthValidation.isGoogleSignInCancelled(error)

    /** Converts Google sign-in failures into user-facing messages without exposing tokens. */
    fun googleSignInMessage(error: Throwable): String =
        FirebaseAuthValidation.googleSignInMessage(error)

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

    fun isGoogleSignInCancelled(error: Throwable): Boolean =
        error is GetCredentialCancellationException

    fun googleSignInMessage(error: Throwable): String =
        when (error) {
            is GetCredentialCancellationException -> "Google sign-in was canceled."
            is NoCredentialException ->
                "No eligible Google account is available on this device."
            is GoogleIdTokenParsingException ->
                "Google sign-in couldn't be verified. Try again."
            is FirebaseAuthException -> googleSignInMessage(error.errorCode)
            else -> "Google sign-in failed. Please try again."
        }

    fun googleSignInMessage(errorCode: String?): String =
        when (errorCode) {
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "Google sign-in is not enabled for this Firebase project."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                "This email already uses another sign-in method. Sign in with that method first."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" ->
                "This Google account is already linked to another account."
            "ERROR_INVALID_CREDENTIAL" ->
                "Google sign-in couldn't be verified. Try again."
            "ERROR_TOO_MANY_REQUESTS" ->
                "Too many attempts. Wait a little and try again."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Couldn't connect to Firebase. Check your internet connection and try again."
            else -> "Google sign-in failed. Please try again."
        }
}
