package io.naira.assistant.presentation.main.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
internal fun MainNavigationBar(
    onUnavailable: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = NavigationBarDefaults.windowInsets,
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Home") },
            modifier = Modifier.testTag("home-nav-home"),
        )
        NavigationBarItem(
            selected = false,
            onClick = { onUnavailable("Chat") },
            icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null) },
            label = { Text("Chat") },
            modifier = Modifier.testTag("home-nav-chat"),
        )
        Spacer(Modifier.weight(1f))
        NavigationBarItem(
            selected = false,
            onClick = { onUnavailable("Camera") },
            icon = {
                CameraGlyph(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            label = { Text("Camera") },
            modifier = Modifier.testTag("home-nav-camera"),
        )
        NavigationBarItem(
            selected = false,
            onClick = { onUnavailable("Library") },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Library") },
            modifier = Modifier.testTag("home-nav-library"),
        )
    }
}

