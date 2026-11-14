@file:Suppress("SpellCheckingInspection")

package io.naira.assistant.presentation.auth.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.naira.assistant.presentation.auth.component.AuthFieldShape
import io.naira.assistant.presentation.auth.component.AuthFooter
import io.naira.assistant.presentation.auth.component.AuthHeader
import io.naira.assistant.presentation.auth.component.AuthIllustration
import io.naira.assistant.presentation.auth.component.AuthPrimaryButton
import io.naira.assistant.presentation.auth.component.AuthScaffold
import io.naira.assistant.presentation.auth.component.GoogleButton
import io.naira.assistant.presentation.auth.component.PasswordVisibilityToggle
import io.naira.assistant.presentation.auth.component.SocialDivider
import io.naira.assistant.presentation.auth.component.authFieldColors
import io.naira.assistant.presentation.auth.component.showFeedback
import io.naira.assistant.presentation.auth.component.stripWhitespace
import io.naira.assistant.presentation.auth.component.validateEmail
import io.naira.assistant.services.auth.FirebaseAuthService
import kotlinx.coroutines.launch

/**
 * Firebase Auth signs the new account in immediately after successful registration.
 */
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    authService: FirebaseAuthService = remember { FirebaseAuthService() },
    onBack: (() -> Unit)? = null,
    onRegistered: (() -> Unit)? = null,
    onLogin: (() -> Unit)? = null,
) {
    var email by rememberSaveable { mutableStateOf("") }
    // Passwords are deliberately NOT saveable: they would be written into the
    // saved-state bundle. Trade-off: rotating the device clears the password
    // field, and the user must retype. This is a conscious security/UX choice.
    var password by remember { mutableStateOf("") }
    var obscure by rememberSaveable { mutableStateOf(true) }
    // Not saveable: a restored "loading = true" would lock the form forever.
    var loading by remember { mutableStateOf(false) }
    // Not saveable: pairs with `password` being non-saveable. If this survived
    // a rotation but the password did not, the user would see "Password is
    // required" for a field they never emptied. Resetting on rotation gives a
    // clean slate — email preserved, password empty, no phantom errors.
    var submitAttempted by remember { mutableStateOf(false) }

    // `email` is already whitespace-free (stripWhitespace runs on every
    // keystroke), so no `.trim()` here.
    val emailError = if (submitAttempted) validateEmail(email) else null
    val passwordError = if (submitAttempted) authService.passwordError(password) else null

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }

    fun submit() {
        if (loading) return
        submitAttempted = true

        // Validate BEFORE dismissing the keyboard — otherwise the user sees
        // errors but has to re-tap the field to fix them.
        val currentEmailError = validateEmail(email)
        val currentPasswordError = authService.passwordError(password)
        if (currentEmailError != null || currentPasswordError != null) {
            return
        }

        // Validation passed → dismiss the keyboard and run the async call.
        focusManager.clearFocus()
        loading = true
        scope.launch {
            try {
                authService.register(email = email, password = password)
                    .onSuccess {
                        onRegistered?.invoke() ?: scope.showFeedback(
                            snackbarHostState,
                            "Account created — you're signed in.",
                        )
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
        onNavigate = onBack,
        navigationEnabled = !loading,
    ) {
        AuthIllustration()
        AuthHeader(title = "Register", subtitle = "Create an account to get started")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.stripWhitespace() },
            enabled = !loading,
            isError = emailError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
            leadingIcon = { Icon(Icons.Outlined.MailOutline, contentDescription = null) },
            placeholder = { Text("Email") },
            supportingText = { emailError?.let { Text(it) } },
            shape = AuthFieldShape,
            colors = authFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentType = ContentType.EmailAddress },
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            enabled = !loading,
            isError = passwordError != null,
            singleLine = true,
            visualTransformation = if (obscure) PasswordVisualTransformation()
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = { PasswordVisibilityToggle(obscure) { obscure = !obscure } },
            placeholder = { Text("Password") },
            supportingText = {
                Text(passwordError ?: "At least 8 characters, with a letter and a number.")
            },
            shape = AuthFieldShape,
            colors = authFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocus)
                .semantics { contentType = ContentType.NewPassword },
        )
        Spacer(Modifier.height(16.dp))

        AuthPrimaryButton(text = "Register", loading = loading, onClick = { submit() })
        Spacer(Modifier.height(24.dp))

        SocialDivider()
        Spacer(Modifier.height(24.dp))

        GoogleButton(
            enabled = !loading,
            onClick = {
                focusManager.clearFocus()
                scope.showFeedback(snackbarHostState, "Google sign-in isn't set up yet.")
            },
        )
        Spacer(Modifier.height(28.dp))

        AuthFooter(
            prompt = "Already have an account?",
            action = "Login",
            onClick = onLogin,
            enabled = !loading,
        )
    }
}