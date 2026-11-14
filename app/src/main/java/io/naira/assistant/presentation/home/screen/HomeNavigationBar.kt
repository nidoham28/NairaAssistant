package io.naira.assistant.presentation.home.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeNavigationBar(onUnavailable: (String) -> Unit) {
    NavigationBar(
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
        LongMicNavigationItem(
            onClick = { onUnavailable("Voice input") },
            modifier = Modifier
                .weight(1f)
                .testTag("home-nav-long-mic"),
        )
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

@Composable
private fun LongMicNavigationItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .heightIn(min = 64.dp)
            .clickable(onClick = onClick, role = Role.Button)
            .semantics(mergeDescendants = true) {
                stateDescription = "Unavailable. Voice input is not available yet."
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            MicrophoneGlyph(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Long mic",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}

@Composable
private fun MicrophoneGlyph(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier) {
        val strokeWidth = size.width * 0.08f
        val capsuleWidth = size.width * 0.26f
        val capsuleHeight = size.height * 0.48f
        drawRoundRect(
            color = color,
            topLeft = Offset((size.width - capsuleWidth) / 2f, size.height * 0.08f),
            size = Size(capsuleWidth, capsuleHeight),
            cornerRadius = CornerRadius(capsuleWidth / 2f),
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.23f, size.height * 0.30f),
            size = Size(size.width * 0.54f, size.height * 0.50f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f, size.height * 0.80f),
            end = Offset(size.width / 2f, size.height * 0.94f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.34f, size.height * 0.94f),
            end = Offset(size.width * 0.66f, size.height * 0.94f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
internal fun CameraGlyph(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier) {
        val strokeWidth = size.width * 0.08f
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.10f, size.height * 0.32f),
            size = Size(size.width * 0.80f, size.height * 0.56f),
            cornerRadius = CornerRadius(size.width * 0.10f),
            style = Stroke(width = strokeWidth),
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.24f, size.height * 0.20f),
            size = Size(size.width * 0.25f, size.height * 0.16f),
            cornerRadius = CornerRadius(size.width * 0.04f),
        )
        drawCircle(
            color = color,
            radius = size.width * 0.16f,
            center = Offset(size.width * 0.56f, size.height * 0.59f),
            style = Stroke(width = strokeWidth),
        )
    }
}
