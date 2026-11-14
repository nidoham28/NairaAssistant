package io.naira.assistant.services.auth

import androidx.credentials.exceptions.GetCredentialCancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseAuthValidationTest {
    @Test
    fun passwordPolicyRequiresLengthLettersAndDigits() {
        assertNotNull(FirebaseAuthValidation.passwordError("short1"))
        assertNotNull(FirebaseAuthValidation.passwordError("12345678"))
        assertNotNull(FirebaseAuthValidation.passwordError("abcdefgh"))
        assertNull(FirebaseAuthValidation.passwordError("naira2026"))
    }

    @Test
    fun credentialErrorsUseTheSameNonSpecificMessage() {
        val wrongPassword = FirebaseAuthValidation.friendlyMessage("ERROR_WRONG_PASSWORD")
        val unknownEmail = FirebaseAuthValidation.friendlyMessage("ERROR_USER_NOT_FOUND")

        assertEquals(wrongPassword, unknownEmail)
    }

    @Test
    fun disabledEmailProviderIsReportedClearly() {
        assertEquals(
            "Email/password sign-in is not enabled for this Firebase project.",
            FirebaseAuthValidation.friendlyMessage("ERROR_OPERATION_NOT_ALLOWED"),
        )
    }

    @Test
    fun googleSignInCancellationIsNotTreatedAsAnError() {
        assertTrue(
            FirebaseAuthValidation.isGoogleSignInCancelled(
                GetCredentialCancellationException("User canceled"),
            ),
        )
        assertFalse(FirebaseAuthValidation.isGoogleSignInCancelled(IllegalStateException()))
    }

    @Test
    fun googleProviderErrorsAreSpecificAndDoNotReuseEmailProviderMessage() {
        assertEquals(
            "Google sign-in is not enabled for this Firebase project.",
            FirebaseAuthValidation.googleSignInMessage("ERROR_OPERATION_NOT_ALLOWED"),
        )
        assertEquals(
            "This email already uses another sign-in method. Sign in with that method first.",
            FirebaseAuthValidation.googleSignInMessage(
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL",
            ),
        )
    }
}
