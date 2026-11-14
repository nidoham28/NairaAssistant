package io.naira.assistant.presentation.main.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CameraAlt
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
    selectedDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = NavigationBarDefaults.windowInsets,
    ) {
        NavigationBarItem(
            selected = selectedDestination == MainDestination.Home,
            onClick = { onDestinationSelected(MainDestination.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Home") },
            modifier = Modifier.testTag("home-nav-home"),
        )
        NavigationBarItem(
            selected = selectedDestination == MainDestination.Chat,
            onClick = { onDestinationSelected(MainDestination.Chat) },
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
            label = { Text("Chat") },
            modifier = Modifier.testTag("home-nav-chat"),
        )
        Spacer(Modifier.weight(1f))
        NavigationBarItem(
            selected = selectedDestination == MainDestination.Camera,
            onClick = { onDestinationSelected(MainDestination.Camera) },
            icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = null) },
            label = { Text("Camera") },
            modifier = Modifier.testTag("home-nav-camera"),
        )
        NavigationBarItem(
            selected = selectedDestination == MainDestination.Library,
            onClick = { onDestinationSelected(MainDestination.Library) },
            icon = { Icon(Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = null) },
            label = { Text("Library") },
            modifier = Modifier.testTag("home-nav-library"),
        )
    }
}

internal enum class MainDestination {
    Home,
    Chat,
    Camera,
    Library,
}

