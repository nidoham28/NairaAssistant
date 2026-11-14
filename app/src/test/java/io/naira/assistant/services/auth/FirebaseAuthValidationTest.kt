package io.naira.assistant.services.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
}
