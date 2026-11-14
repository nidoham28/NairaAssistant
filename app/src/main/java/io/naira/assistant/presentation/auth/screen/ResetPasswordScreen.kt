@file:Suppress("SpellCheckingInspection")

package io.naira.assistant.presentation.auth.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.naira.assistant.presentation.auth.component.AuthFieldShape
import io.naira.assistant.presentation.auth.component.AuthHeader
import io.naira.assistant.presentation.auth.component.AuthIllustration
import io.naira.assistant.presentation.auth.component.AuthPrimaryButton
import io.naira.assistant.presentation.auth.component.AuthScaffold
import io.naira.assistant.presentation.auth.component.authFieldColors
import io.naira.assistant.presentation.auth.component.showFeedback
import io.naira.assistant.presentation.auth.component.stripWhitespace
import io.naira.assistant.presentation.auth.component.validateEmail
import io.naira.assistant.services.auth.FirebaseAuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Requests Firebase's hosted password-reset email action.
 *
 * The email link completes the password reset; this screen does not handle reset credentials
 * or change a password locally.
 */
@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    authService: FirebaseAuthService = remember { FirebaseAuthService() },
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var sent by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var cooldown by remember { mutableIntStateOf(0) }

    LaunchedEffect(cooldown) {
        if (cooldown > 0) {
            delay(1.seconds)
            cooldown--
        }
    }

    fun leaveScreen() {
        if (loading) return
        if (sent) {
            sent = false
        } else {
            onBack()
        }
    }

    BackHandler(enabled = !loading) { leaveScreen() }

    fun sendResetEmail() {
        if (loading || (sent && cooldown > 0)) return
        emailError = validateEmail(email)
        if (emailError != null) return

        focusManager.clearFocus()
        loading = true
        scope.launch {
            try {
                authService.sendPasswordResetEmail(email)
                    .onSuccess {
                        sent = true
                        cooldown = 60
                    }
                    .onFailure {
                        scope.showFeedback(
                            snackbarHostState,
                            authService.friendlyMessage(it),
                            isError = true,
                        )
                    }
            } finally {
                loading = false
            }
        }
    }

    AuthScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onNavigate = { leaveScreen() },
        navigationEnabled = !loading,
    ) {
        AuthIllustration(height = 220.dp)
        if (sent) {
            AuthHeader(
                title = "Check your email",
                subtitle = "If an account exists for $email, Firebase has sent a password reset link. " +
                    "Follow the link to reset your password, then return here to sign in.",
            )
            AuthPrimaryButton(
                text = if (cooldown > 0) "Resend in ${cooldown}s" else "Resend reset email",
                loading = loading,
                enabled = cooldown == 0,
                onClick = { sendResetEmail() },
            )
        } else {
            AuthHeader(
                title = "Forgot password?",
                subtitle = "Enter your email and Firebase will send instructions to reset your password.",
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it.stripWhitespace()
                    if (emailError != null) emailError = null
                },
                enabled = !loading,
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { sendResetEmail() }),
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                placeholder = { Text("Email") },
                supportingText = { emailError?.let { Text(it) } },
                shape = AuthFieldShape,
                colors = authFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.EmailAddress },
            )
            AuthPrimaryButton(
                text = "Send reset email",
                loading = loading,
                onClick = { sendResetEmail() },
            )
        }
    }
}
