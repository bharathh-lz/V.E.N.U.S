package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShieldAmber
import com.example.ui.theme.ShieldBlueDial
import com.example.ui.theme.ShieldBorder
import com.example.ui.theme.ShieldCoreWhite
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.ShieldCyanBright
import com.example.ui.theme.ShieldCyanGlow
import com.example.ui.theme.ShieldDarkBezel
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * S.H.I.E.L.D. OS Top Diagnostic Header:
 * - Falcon eagle S.H.I.E.L.D. emblem badge
 * - "S.H.I.E.L.D. OS" tactical banner & version tag
 * - Bold digital LCD clock readout ("08:55 AM") with date stamp
 * - Quick power diagnostics strip (DOWN, RSTRT, HBRN, LOCK, ABOUT)
 */
@Composable
fun ShieldTopHeader(
    onOpenSettings: () -> Unit,
    onToggleEngine: () -> Unit,
    isEngineRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val currentTime = remember {
        val sdfTime = SimpleDateFormat("hh:mm", Locale.US)
        val sdfAmPm = SimpleDateFormat("a", Locale.US)
        val sdfDate = SimpleDateFormat("dd-MMM., EEEE", Locale.US)
        val now = Date()
        Triple(sdfTime.format(now), sdfAmPm.format(now).uppercase(), sdfDate.format(now).uppercase())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("shield_top_header")
    ) {
        // Upper Status Bar with Emblem & Quick Diagnostic Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // S.H.I.E.L.D. Emblem Badge & OS Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VenusSurfaceDark)
                    .border(1.dp, ShieldBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                // Circular Eagle Emblem Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(ShieldCyan.copy(alpha = 0.4f), ShieldDarkBezel)
                            )
                        )
                        .border(1.2.dp, ShieldCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "S.H.I.E.L.D. Falcon Emblem",
                        tint = ShieldCyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "V.E.N.U.S AI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCoreWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "VER 12.0 • NEURAL HUD CORE",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCyanGlow
                    )
                }
            }

            // Quick Diagnostic Action Buttons (DOWN, RSTRT, LOCK, SETTINGS)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ShieldQuickActionBtn(
                    label = "LOCK",
                    icon = Icons.Default.Lock,
                    tint = if (isEngineRunning) ShieldCyan else VenusTextMuted,
                    onClick = onToggleEngine
                )
                ShieldQuickActionBtn(
                    label = "SYS",
                    icon = Icons.Default.Settings,
                    tint = ShieldCyanGlow,
                    onClick = onOpenSettings
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large Digital Stencil Clock & Date Stamp (Matching image top-left "08:55 AM")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentTime.third, // Date (e.g. 29-OCT., TUESDAY)
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = ShieldCyanGlow,
                    letterSpacing = 1.2.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = currentTime.first, // Time (e.g. 08:55)
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCoreWhite,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentTime.second, // AM / PM
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCyanBright,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Right-hand Live System Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = VenusSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isEngineRunning) ShieldGreen else ShieldAmber)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isEngineRunning) "DEFENSE: ONLINE" else "DEFENSE: STANDBY",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isEngineRunning) ShieldGreen else ShieldAmber
                        )
                    }
                    Text(
                        text = "CLEARANCE: LEVEL 8",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = VenusTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ShieldQuickActionBtn(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = VenusSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder),
        modifier = Modifier.size(width = 44.dp, height = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                fontSize = 6.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = tint
            )
        }
    }
}

/**
 * Left-Hand Tactical App Launcher Dock (Sci-Fi Angled Chevron Tabs)
 * Directly inspired by the left-side panel tabs from the user's reference image:
 * - BROWSER, EXPLORER, AUDIO, COMMS, GEMINI AI, SHIELD
 */
@Composable
fun ShieldTacticalDock(
    onSelectAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple("BROWSER", Icons.Default.Language, "Search Web & News"),
        Triple("EXPLORER", Icons.Default.Folder, "Smart Notes & Vault"),
        Triple("AUDIO", Icons.Default.MusicNote, "Soundscapes & Engine"),
        Triple("COMMS", Icons.Default.Phone, "Direct Voice Calls"),
        Triple("GEMINI", Icons.Default.SmartToy, "Neural Intelligence"),
        Triple("VENUS", Icons.Default.Security, "Clearance & Diagnostics")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { (label, icon, desc) ->
            Surface(
                onClick = { onSelectAction(label) },
                shape = RoundedCornerShape(6.dp),
                color = VenusSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("shield_dock_${label.lowercase()}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp, vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = desc,
                        tint = ShieldCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = label,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCoreWhite,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Right-Hand HUD Gauges & Weather Telemetry Row
 * Recreates the right side of the user's image:
 * - Weather card: 31°F Cloudy with atmospheric readings
 * - Speed / RPM circular gauge with glowing blue needle
 * - RAM / Memory circular gauge with neon dial arc
 */
@Composable
fun ShieldTelemetryGauges(
    rpm: Int,
    isEngineRunning: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Left Panel: Weather HUD Telemetry (31°F Cloudy)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = VenusSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder),
            modifier = Modifier.weight(1.3f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATMOSPHERE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCyanGlow
                    )
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Cloudy Weather",
                        tint = ShieldCyanBright,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "31°F",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCoreWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CLOUDY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCyanBright,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = "Humidity: 77% • Wind: CALM",
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VenusTextSecondary
                )
                Text(
                    text = "Pressure: 29.94 in • Sun: 6:00 PM",
                    fontSize = 7.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VenusTextMuted
                )
            }
        }

        // Right Panel: Dual Sci-Fi Circular Gauges (Speed/RPM & RAM Memory)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = VenusSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder),
            modifier = Modifier.weight(1.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gauge 1: Tachometer / Speed Dial
                ShieldCircularDial(
                    title = "SPEED",
                    subValue = if (isEngineRunning) "$rpm" else "0",
                    unit = "RPM",
                    progress = if (isEngineRunning) (rpm / 9000f).coerceIn(0f, 1f) else 0f,
                    dialColor = ShieldBlueDial
                )

                // Gauge 2: RAM Memory Telemetry (Used: 3.5GB)
                ShieldCircularDial(
                    title = "RAM",
                    subValue = "3.5",
                    unit = "GB",
                    progress = 0.72f,
                    dialColor = ShieldCyan
                )
            }
        }
    }
}

/**
 * Individual Sci-Fi Circular Dial Gauge with glowing needle sweep
 */
@Composable
private fun ShieldCircularDial(
    title: String,
    subValue: String,
    unit: String,
    progress: Float,
    dialColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) - 3.dp.toPx()

                // Background track
                drawArc(
                    color = Color(0xFF141E2E),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active glowing gauge sweep arc
                drawArc(
                    color = dialColor,
                    startAngle = 135f,
                    sweepAngle = 270f * progress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Needle line pointer
                val needleAngle = 135f + (270f * progress)
                val rad = Math.toRadians(needleAngle.toDouble())
                val needleLen = radius * 0.75f
                val nx = center.x + (needleLen * cos(rad)).toFloat()
                val ny = center.y + (needleLen * sin(rad)).toFloat()

                drawLine(
                    color = ShieldCoreWhite,
                    start = center,
                    end = Offset(nx, ny),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center pivot dot
                drawCircle(color = ShieldCoreWhite, radius = 2.dp.toPx(), center = center)
            }

            // Mini center value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = subValue,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = ShieldCoreWhite
                )
            }
        }

        Text(
            text = "$title ($unit)",
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = VenusTextMuted
        )
    }
}

/**
 * Bottom HUD Telemetry Strip
 * Inspired by the bottom telemetry in the reference image:
 * - WiFi 88% circular arc gauge
 * - Battery 100% AC Line power gauge
 * - Real-time pulse waveform
 * - Media Transport bar (⏮️ ▶️ ⏭️)
 */
@Composable
fun ShieldBottomTelemetry(
    onPlayMedia: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = VenusSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, ShieldBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shield_bottom_telemetry")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Wifi 88% Circular Mini Arc
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.minDimension / 2f - 2.dp.toPx()
                        drawArc(
                            color = ShieldCyan,
                            startAngle = 180f,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "WiFi Status",
                        tint = ShieldCyanBright,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = "WIFI: 88%",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ShieldCyanGlow
                    )
                    Text(
                        text = "SECURE PROTOCOL",
                        fontSize = 6.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = VenusTextMuted
                    )
                }
            }

            // Center: Sci-Fi Pulse Waveform Graph
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(22.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val midY = h / 2f

                    for (x in 0..w.toInt() step 4) {
                        val rad = Math.toRadians((wavePhase + (x * 8f)).toDouble())
                        val waveH = (sin(rad) * (h * 0.4f)).toFloat()
                        drawLine(
                            color = ShieldCyanBright.copy(alpha = 0.7f),
                            start = Offset(x.toFloat(), midY - waveH),
                            end = Offset(x.toFloat(), midY + waveH),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }
            }

            // Right: Media Transport Mini Controls (⏮️ ▶️ ⏭️)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = onPlayMedia,
                    shape = CircleShape,
                    color = VenusSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ShieldCyan),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Previous Track",
                            tint = ShieldCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Surface(
                    onClick = onPlayMedia,
                    shape = CircleShape,
                    color = ShieldCyan,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Audio",
                            tint = ShieldDarkBezel,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    onClick = onPlayMedia,
                    shape = CircleShape,
                    color = VenusSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ShieldCyan),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Next Track",
                            tint = ShieldCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
