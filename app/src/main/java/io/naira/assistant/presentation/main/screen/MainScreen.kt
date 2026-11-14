package io.naira.assistant.presentation.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.naira.assistant.presentation.main.component.MainContent
import io.naira.assistant.presentation.main.component.MainNavigationBar
import io.naira.assistant.presentation.main.component.MicrophoneGlyph
import kotlinx.coroutines.launch

/**
 * Renders the Home destination and reports unavailable actions without starting microphone capture.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showUnavailable(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(unavailableDestinationMessage(message))
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "NAIRA",
                        modifier = Modifier.testTag("home-screen-brand"),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showUnavailable("Settings") },
                        modifier = Modifier.testTag("open-settings"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showUnavailable("Notifications") },
                        modifier = Modifier.testTag("open-notifications"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            Box {
                MainNavigationBar(
                    onUnavailable = { showUnavailable(it) },
                    modifier = Modifier.testTag("home-bottom-navigation"),
                )
                FloatingActionButton(
                    onClick = { showUnavailable("Voice input") },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-20).dp)
                        .size(72.dp)
                        .testTag("home-action-long-mic")
                        .semantics {
                            contentDescription = "Long mic. Voice input is unavailable."
                        },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    MicrophoneGlyph(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        },
    ) { innerPadding ->
        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onOpenChat = { showUnavailable("Chat") },
            onOpenCamera = { showUnavailable("Camera") },
            onOpenLibrary = { showUnavailable("Library") },
        )
    }
}

private fun unavailableDestinationMessage(label: String): String = when (label) {
    "Notifications", "Settings" -> "$label aren't available yet."
    else -> "$label isn't available yet."
}
