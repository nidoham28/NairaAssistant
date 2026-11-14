package io.naira.assistant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = NairaPrimaryLight,
    onPrimary = NairaOnPrimaryLight,
    primaryContainer = NairaSurfaceVariantLight,
    onPrimaryContainer = NairaOnSurfaceLight,
    secondary = NairaSecondaryLight,
    onSecondary = NairaOnSecondaryLight,
    secondaryContainer = NairaSurfaceVariantLight,
    onSecondaryContainer = NairaOnSurfaceLight,
    tertiary = NairaTertiaryLight,
    onTertiary = NairaOnSecondaryLight,
    background = NairaBackgroundLight,
    onBackground = NairaOnBackgroundLight,
    surface = NairaSurfaceLight,
    onSurface = NairaOnSurfaceLight,
    surfaceVariant = NairaSurfaceVariantLight,
    onSurfaceVariant = NairaOnSurfaceLight,
    error = NairaError,
    onError = NairaOnError,
    errorContainer = NairaErrorContainer,
    onErrorContainer = NairaError,
    outline = NairaOutline,
    outlineVariant = NairaOutlineVariant,
    scrim = NairaOnBackgroundLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NairaPrimaryDark,
    onPrimary = NairaOnPrimaryDark,
    primaryContainer = NairaSurfaceVariantDark,
    onPrimaryContainer = NairaOnSurfaceDark,
    secondary = NairaSecondaryDark,
    onSecondary = NairaOnSecondaryDark,
    secondaryContainer = NairaSurfaceVariantDark,
    onSecondaryContainer = NairaOnSurfaceDark,
    tertiary = NairaTertiaryDark,
    onTertiary = NairaOnSecondaryDark,
    background = NairaBackgroundDark,
    onBackground = NairaOnBackgroundDark,
    surface = NairaSurfaceDark,
    onSurface = NairaOnSurfaceDark,
    surfaceVariant = NairaSurfaceVariantDark,
    onSurfaceVariant = NairaOnSurfaceDark,
    error = NairaError,
    onError = NairaOnError,
    errorContainer = NairaErrorContainer,
    onErrorContainer = NairaOnError,
    outline = NairaOutline,
    outlineVariant = NairaOutlineVariant,
    scrim = NairaOnBackgroundDark
)

@Composable
fun NairaAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NairaAssistantTypography,
        content = content
    )
}