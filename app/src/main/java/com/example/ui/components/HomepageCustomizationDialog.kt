package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusTheme
import com.example.ui.theme.VenusThemePreset
import com.example.ui.theme.VenusThemePresets

/**
 * Full content view for Homepage & Aesthetic Theme Customization.
 * Supports:
 * 1. Aesthetic Theme Presets (Cyberpunk HUD, Minimalist Obsidian, Classic Tactical, Titanium Stealth, Neon Viper)
 * 2. Homepage Module Visibility Toggles (Arc Reactor Orb, Ignition Switch, HUD Telemetry, Quick Commands Grid, Status Ticker, Recent Transcript)
 * 3. Arc Reactor Scale Slider (0.7x to 1.3x)
 * 4. Greeting Style Selector (Tactical, Casual, Minimal)
 * 5. Full Room Database Persistence
 */
@Composable
fun HomepageCustomizationContent(
    viewModel: VenusViewModel,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = VenusTheme.current

    val themePreset by viewModel.themePreset.collectAsState()
    val showOrb by viewModel.showOrb.collectAsState()
    val showIgnitionSwitch by viewModel.showIgnitionSwitch.collectAsState()
    val showHudMetrics by viewModel.showHudMetrics.collectAsState()
    val showQuickCommandGrid by viewModel.showQuickCommandGrid.collectAsState()
    val showLiveStatusTicker by viewModel.showLiveStatusTicker.collectAsState()
    val showRecentDialoguePreview by viewModel.showRecentDialoguePreview.collectAsState()
    val orbScale by viewModel.orbScale.collectAsState()
    val greetingStyle by viewModel.homepageGreetingStyle.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("homepage_customization_content")
    ) {
        // Header Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.primary.copy(alpha = 0.15f))
                        .border(1.dp, theme.primary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme Customization",
                        tint = theme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "HOMEPAGE CUSTOMIZATION",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary
                    )
                    Text(
                        text = "ROOM DB PERSISTENCE • REAL-TIME THEME ENGINE",
                        fontSize = 9.sp,
                        color = theme.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            if (onClose != null) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(theme.surfaceVariant)
                        .testTag("btn_close_customization")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 1: AESTHETIC THEME PRESETS
        Text(
            text = "AESTHETIC PRESETS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = theme.textSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VenusThemePresets.forEach { preset ->
                ThemePresetCard(
                    preset = preset,
                    isSelected = themePreset.equals(preset.id, ignoreCase = true),
                    onSelect = { viewModel.updateThemePreset(preset.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // SECTION 2: HOMEPAGE MODULE TOGGLES
        Text(
            text = "HOMEPAGE MODULE LAYOUT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = theme.textSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = theme.surface),
            border = BorderStroke(1.dp, theme.cardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Arc Reactor Orb Switch
                ModuleSwitchRow(
                    title = "Arc Reactor / Neural Orb",
                    description = "Holographic multi-ring animated core with RMS audio response",
                    icon = Icons.Default.GraphicEq,
                    checked = showOrb,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = isChecked,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_show_orb"
                )

                // Arc Reactor Scaling Slider (Only visible if Orb is enabled)
                AnimatedVisibility(visible = showOrb) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, theme.cardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reactor Orb Scale",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary
                            )
                            Text(
                                text = "${(orbScale * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                        }

                        Slider(
                            value = orbScale,
                            onValueChange = { newScale ->
                                viewModel.updateHomepageLayout(
                                    showOrb = showOrb,
                                    showIgnitionSwitch = showIgnitionSwitch,
                                    showHudMetrics = showHudMetrics,
                                    showQuickCommandGrid = showQuickCommandGrid,
                                    showLiveStatusTicker = showLiveStatusTicker,
                                    showRecentDialoguePreview = showRecentDialoguePreview,
                                    orbScale = newScale,
                                    greetingStyle = greetingStyle
                                )
                            },
                            valueRange = 0.7f..1.3f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.primary,
                                activeTrackColor = theme.primary,
                                inactiveTrackColor = theme.cardBorder
                            ),
                            modifier = Modifier.testTag("slider_orb_scale")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Compact (70%)", fontSize = 9.sp, color = theme.textMuted)
                            Text("Standard (100%)", fontSize = 9.sp, color = theme.textMuted)
                            Text("Large (130%)", fontSize = 9.sp, color = theme.textMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Supercar Ignition Switch
                ModuleSwitchRow(
                    title = "Supercar Ignition Start Switch",
                    description = "Safety-flip cover with cranking sound & V8 start sequence",
                    icon = Icons.Default.PowerSettingsNew,
                    checked = showIgnitionSwitch,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = isChecked,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_ignition_switch"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // HUD Telemetry Gauges
                ModuleSwitchRow(
                    title = "HUD Telemetry & RPM Gauges",
                    description = "Dual circular dials for Engine RPM, RAM load & weather telemetry",
                    icon = Icons.Default.Speed,
                    checked = showHudMetrics,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = isChecked,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_hud_metrics"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Command Grid
                ModuleSwitchRow(
                    title = "Neural Tools Quick Launch Grid",
                    description = "One-tap shortcuts for Vision AI, Code Studio, Summarizer & Games",
                    icon = Icons.Default.Dashboard,
                    checked = showQuickCommandGrid,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = isChecked,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_command_grid"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live Status & Lock-Screen Ticker
                ModuleSwitchRow(
                    title = "Continuous Listening & Lock Ticker",
                    description = "Displays wake-word daemon status and lock-screen indicator bar",
                    icon = Icons.Default.Visibility,
                    checked = showLiveStatusTicker,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = isChecked,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_status_ticker"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Recent Dialogue Response Card
                ModuleSwitchRow(
                    title = "Recent Dialogue & Voice Replay Card",
                    description = "Shows the active speech prompt and assistant reply card",
                    icon = Icons.Default.GraphicEq,
                    checked = showRecentDialoguePreview,
                    onCheckedChange = { isChecked ->
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = isChecked,
                            orbScale = orbScale,
                            greetingStyle = greetingStyle
                        )
                    },
                    testTag = "toggle_recent_dialogue"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: GREETING STYLE
        Text(
            text = "GREETING DISPLAY STYLE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = theme.textSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val styles = listOf(
                Triple("TACTICAL", "Tactical HUD", "COMMANDER BHARATH // ONLINE"),
                Triple("CASUAL", "Companion", "Welcome back, Bharath!"),
                Triple("MINIMAL", "Minimal", "V.E.N.U.S Ready")
            )

            styles.forEach { (styleId, title, preview) ->
                val isSelected = greetingStyle.equals(styleId, ignoreCase = true)
                Surface(
                    onClick = {
                        viewModel.updateHomepageLayout(
                            showOrb = showOrb,
                            showIgnitionSwitch = showIgnitionSwitch,
                            showHudMetrics = showHudMetrics,
                            showQuickCommandGrid = showQuickCommandGrid,
                            showLiveStatusTicker = showLiveStatusTicker,
                            showRecentDialoguePreview = showRecentDialoguePreview,
                            orbScale = orbScale,
                            greetingStyle = styleId
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) theme.surfaceVariant else theme.surface,
                    border = BorderStroke(1.dp, if (isSelected) theme.primary else theme.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("greeting_style_$styleId")
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) theme.primary else theme.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = preview,
                            fontSize = 8.sp,
                            color = theme.textMuted,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // SECTION 4: ACTIONS (Reset defaults & Close)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.updateThemePreset("CYBERPUNK")
                    viewModel.updateHomepageLayout(
                        showOrb = true,
                        showIgnitionSwitch = true,
                        showHudMetrics = true,
                        showQuickCommandGrid = true,
                        showLiveStatusTicker = true,
                        showRecentDialoguePreview = true,
                        orbScale = 1.0f,
                        greetingStyle = "TACTICAL"
                    )
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, theme.cardBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_reset_homepage_defaults")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Defaults",
                    tint = theme.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reset Defaults",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary
                )
            }

            if (onClose != null) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("btn_apply_homepage_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Apply",
                        tint = theme.background,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply & View",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.background
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * Aesthetic Preset Card Component with Color Swatches & Selection Status
 */
@Composable
private fun ThemePresetCard(
    preset: VenusThemePreset,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val theme = VenusTheme.current

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) theme.surfaceVariant else theme.surface,
        border = BorderStroke(
            1.2.dp,
            if (isSelected) preset.primary else theme.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preset_card_${preset.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = preset.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) preset.primary else theme.textPrimary
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(preset.primary.copy(alpha = 0.2f))
                                .border(1.dp, preset.primary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = preset.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = preset.description,
                    fontSize = 11.sp,
                    color = theme.textSecondary,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Swatches Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColorDot(color = preset.primary, label = "Primary")
                    ColorDot(color = preset.primaryGlow, label = "Glow")
                    ColorDot(color = preset.secondary, label = "Secondary")
                    ColorDot(color = preset.surface, label = "Surface")
                    ColorDot(color = preset.background, label = "Background")
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Selection Radio or Check Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) preset.primary else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) preset.primary else theme.cardBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = preset.background,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color, label: String) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    )
}

/**
 * Reusable Row with Title, Description, Icon, and Switch
 */
@Composable
private fun ModuleSwitchRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val theme = VenusTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (checked) theme.primary.copy(alpha = 0.15f) else theme.surfaceVariant)
                    .border(
                        1.dp,
                        if (checked) theme.primary.copy(alpha = 0.6f) else theme.cardBorder,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) theme.primary else theme.textMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (checked) theme.textPrimary else theme.textMuted
                )
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = theme.textMuted,
                    lineHeight = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = theme.background,
                checkedTrackColor = theme.primary,
                uncheckedThumbColor = theme.textMuted,
                uncheckedTrackColor = theme.surfaceVariant
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

/**
 * Modal Dialog for Homepage Customization triggered right from the Assistant Screen
 */
@Composable
fun HomepageCustomizationDialog(
    viewModel: VenusViewModel,
    onDismiss: () -> Unit
) {
    val theme = VenusTheme.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = theme.background,
            border = BorderStroke(1.2.dp, theme.primary.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(640.dp)
        ) {
            HomepageCustomizationContent(
                viewModel = viewModel,
                onClose = onDismiss
            )
        }
    }
}
