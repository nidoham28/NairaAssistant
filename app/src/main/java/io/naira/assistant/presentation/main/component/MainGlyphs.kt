package io.naira.assistant.presentation.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun MicrophoneGlyph(
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
