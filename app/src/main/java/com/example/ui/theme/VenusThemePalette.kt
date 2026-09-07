package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class VenusThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val primary: Color,
    val primaryGlow: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
)

val CyberpunkPreset = VenusThemePreset(
    id = "CYBERPUNK",
    name = "Cyberpunk HUD",
    description = "Electric Cyan, holographic glow & carbon chassis",
    primary = Color(0xFF00E5FF),
    primaryGlow = Color(0xFF80D8FF),
    secondary = Color(0xFF00B0FF),
    background = Color(0xFF04070D),
    surface = Color(0xFF08101C),
    surfaceVariant = Color(0xFF0E1A2C),
    cardBorder = Color(0xFF16324F),
    textPrimary = Color(0xFFE1F5FE),
    textSecondary = Color(0xFF80A3BF),
    textMuted = Color(0xFF4A6B88)
)

val MinimalistPreset = VenusThemePreset(
    id = "MINIMALIST",
    name = "Minimalist Obsidian",
    description = "Monochrome titanium slate, clean contrast & pure dark",
    primary = Color(0xFFE2E8F0),
    primaryGlow = Color(0xFFFFFFFF),
    secondary = Color(0xFF94A3B8),
    background = Color(0xFF08090C),
    surface = Color(0xFF12141B),
    surfaceVariant = Color(0xFF1B1F2A),
    cardBorder = Color(0xFF282E3E),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B)
)

val ClassicDarkPreset = VenusThemePreset(
    id = "CLASSIC_DARK",
    name = "Classic Tactical",
    description = "S.H.I.E.L.D. tactical emerald green & telemetry shadows",
    primary = Color(0xFF00E676),
    primaryGlow = Color(0xFF69F0AE),
    secondary = Color(0xFFFFB300),
    background = Color(0xFF050B07),
    surface = Color(0xFF0C1710),
    surfaceVariant = Color(0xFF13241A),
    cardBorder = Color(0xFF1B3B2B),
    textPrimary = Color(0xFFE8F5E9),
    textSecondary = Color(0xFF81C784),
    textMuted = Color(0xFF4E7756)
)

val TitaniumStealthPreset = VenusThemePreset(
    id = "TITANIUM_STEALTH",
    name = "Titanium Stealth",
    description = "Supercar cockpit amber, brushed bronze & carbon fiber",
    primary = Color(0xFFFFB300),
    primaryGlow = Color(0xFFFFD54F),
    secondary = Color(0xFFFF6D00),
    background = Color(0xFF0B0907),
    surface = Color(0xFF17130E),
    surfaceVariant = Color(0xFF241D15),
    cardBorder = Color(0xFF423422),
    textPrimary = Color(0xFFFFF8E1),
    textSecondary = Color(0xFFFFB74D),
    textMuted = Color(0xFF82623B)
)

val NeonViperPreset = VenusThemePreset(
    id = "NEON_VIPER",
    name = "Neon Viper",
    description = "Synthwave ultraviolet, electric purple & laser pink",
    primary = Color(0xFFC084FC),
    primaryGlow = Color(0xFFE879F9),
    secondary = Color(0xFFF43F5E),
    background = Color(0xFF07040B),
    surface = Color(0xFF140D22),
    surfaceVariant = Color(0xFF201336),
    cardBorder = Color(0xFF3B2163),
    textPrimary = Color(0xFFF5EEFF),
    textSecondary = Color(0xFFD8B4FE),
    textMuted = Color(0xFF7E5296)
)

val VenusThemePresets = listOf(
    CyberpunkPreset,
    MinimalistPreset,
    ClassicDarkPreset,
    TitaniumStealthPreset,
    NeonViperPreset
)

fun getVenusPresetById(id: String): VenusThemePreset {
    return VenusThemePresets.find { it.id.equals(id, ignoreCase = true) } ?: CyberpunkPreset
}

val LocalVenusTheme = staticCompositionLocalOf { CyberpunkPreset }

object VenusTheme {
    val current: VenusThemePreset
        @Composable
        @ReadOnlyComposable
        get() = LocalVenusTheme.current
}
