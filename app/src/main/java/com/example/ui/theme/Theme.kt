package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    themePreset: String = "CYBERPUNK",
    content: @Composable () -> Unit,
) {
    val preset = getVenusPresetById(themePreset)

    val dynamicColorScheme = darkColorScheme(
        primary = preset.primary,
        onPrimary = preset.background,
        primaryContainer = preset.surfaceVariant,
        onPrimaryContainer = preset.primaryGlow,
        secondary = preset.secondary,
        onSecondary = Color.White,
        secondaryContainer = preset.surfaceVariant,
        onSecondaryContainer = preset.primary,
        tertiary = preset.primaryGlow,
        onTertiary = preset.background,
        background = preset.background,
        onBackground = preset.textPrimary,
        surface = preset.surface,
        onSurface = preset.textPrimary,
        surfaceVariant = preset.surfaceVariant,
        onSurfaceVariant = preset.textSecondary,
        outline = preset.cardBorder,
        error = VenusRed,
        onError = Color(0xFF601410)
    )

    CompositionLocalProvider(LocalVenusTheme provides preset) {
        MaterialTheme(
            colorScheme = dynamicColorScheme,
            typography = Typography,
            content = content
        )
    }
}

