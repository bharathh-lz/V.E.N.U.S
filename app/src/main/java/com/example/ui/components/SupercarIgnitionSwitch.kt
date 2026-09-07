package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * SupercarIgnitionSwitch: Hypercar-styled ENGINE START / STOP push button.
 * Inspired by modern exotic supercars (Aventador, Chiron, P1, SF90):
 * - Billet aluminum & carbon-fiber bezel styling with machined hex bolts
 * - Integrated 360-degree tachometer ring showing real-time RPM telemetry
 * - Crossplane V8 8-cylinder sequential firing order display
 * - 3D tactile push button with haptic depression response
 * - Dynamic ignition states: Cranking, Rev-up roar, Idle lope, and Standby
 */
@Composable
fun SupercarIgnitionSwitch(
    isEngineStarted: Boolean,
    isIgniting: Boolean,
    engineRpm: Int,
    onToggleEngine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth button depression when pressed
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(100),
        label = "button_depression"
    )

    // Dynamic combustion pulsation for running engine
    val infiniteTransition = rememberInfiniteTransition(label = "supercar_engine_anim")
    val firingPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "firing_pulse"
    )

    // Tachometer needle sweep smoothing
    val smoothRpm by animateFloatAsState(
        targetValue = engineRpm.toFloat(),
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "smooth_rpm"
    )

    // Rotating firing order LED index for High-Performance Core (1-5-3-6-2-4)
    val firingCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isIgniting) 160 else 480, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "firing_cycle"
    )
    val activeCylinderIndex = firingCycle.toInt() % 6

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("supercar_ignition_switch"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer Cockpit Console Housing
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            // Ambient Combustion Halo Glow
            val glowBrush = when {
                isIgniting -> Brush.radialGradient(
                    listOf(
                        Color(0xFFFF9100).copy(alpha = 0.55f * firingPulse),
                        Color(0xFFFF3D00).copy(alpha = 0.30f),
                        Color.Transparent
                    )
                )
                isEngineStarted -> Brush.radialGradient(
                    listOf(
                        Color(0xFFFF1744).copy(alpha = 0.40f * firingPulse),
                        Color(0xFFFF6D00).copy(alpha = 0.18f),
                        Color.Transparent
                    )
                )
                else -> Brush.radialGradient(
                    listOf(
                        VenusCyan.copy(alpha = 0.25f),
                        VenusPurple.copy(alpha = 0.12f),
                        Color.Transparent
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(glowBrush)
            )

            // Outer Machined Billet Bezel with Tachometer Gauge Canvas
            Canvas(modifier = Modifier.size(165.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f - 4.dp.toPx()

                // Background bezel ring
                drawCircle(
                    color = Color(0xFF181820),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Tachometer scale arc (from 135 deg to 405 deg = 270 deg sweep)
                val startAngle = 135f
                val sweepAngle = 270f
                val maxRpm = 9000f
                val rpmProgress = (smoothRpm / maxRpm).coerceIn(0f, 1f)

                // Safe zone (0 - 6500 RPM)
                drawArc(
                    color = Color(0xFF2E2E3E),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * 0.72f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Redline zone (6500 - 9000 RPM)
                drawArc(
                    color = Color(0xFF6B1D22),
                    startAngle = startAngle + (sweepAngle * 0.72f),
                    sweepAngle = sweepAngle * 0.28f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active Tachometer dynamic arc
                val activeColor = when {
                    isIgniting -> Color(0xFFFF9100)
                    smoothRpm > 6500f -> Color(0xFFFF1744)
                    isEngineStarted -> Color(0xFFFF5252)
                    else -> VenusCyan
                }

                if (smoothRpm > 50f) {
                    drawArc(
                        color = activeColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle * rpmProgress,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 4 Allen Head Hex Bolts at 45° intervals
                val boltAngles = listOf(45.0, 135.0, 225.0, 315.0)
                val boltDistance = radius - 6.dp.toPx()
                for (angleDeg in boltAngles) {
                    val rad = Math.toRadians(angleDeg)
                    val bx = center.x + (boltDistance * cos(rad)).toFloat()
                    val by = center.y + (boltDistance * sin(rad)).toFloat()
                    drawCircle(color = Color(0xFF424250), radius = 3.dp.toPx(), center = Offset(bx, by))
                    drawCircle(color = Color(0xFF141418), radius = 1.8.dp.toPx(), center = Offset(bx, by))
                }
            }

            // Central Hypercar Push Button
            Surface(
                onClick = onToggleEngine,
                interactionSource = interactionSource,
                shape = CircleShape,
                color = when {
                    isIgniting -> Color(0xFF2A1208)
                    isEngineStarted -> Color(0xFF240A0D)
                    else -> Color(0xFF1A1A24)
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isEngineStarted || isIgniting) 2.5.dp else 2.dp,
                    brush = when {
                        isIgniting -> Brush.linearGradient(listOf(Color(0xFFFF9100), Color(0xFFFF3D00)))
                        isEngineStarted -> Brush.linearGradient(listOf(Color(0xFFFF1744), Color(0xFFFF5252)))
                        else -> Brush.linearGradient(listOf(VenusCyan, VenusLavender.copy(alpha = 0.5f)))
                    }
                ),
                modifier = Modifier
                    .size(134.dp)
                    .scale(buttonScale)
                    .testTag("engine_start_stop_button")
                    .testTag("supercar_start_stop_button")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = when {
                                    isIgniting -> listOf(
                                        Color(0xFFFF6D00).copy(alpha = 0.28f),
                                        Color.Transparent
                                    )
                                    isEngineStarted -> listOf(
                                        Color(0xFFFF1744).copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                    else -> listOf(
                                        VenusCyan.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top Label: IGNITION ARC
                        Text(
                            text = if (isIgniting) "IGNITING" else "IGNITION ARC",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp,
                            color = when {
                                isIgniting -> Color(0xFFFFB74D)
                                isEngineStarted -> Color(0xFFFF8A80)
                                else -> VenusTextSecondary
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Center Icon: Illuminated Supercar Power / Ignition
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = if (isEngineStarted) "Stop Engine" else "Start Engine",
                            tint = when {
                                isIgniting -> Color(0xFFFFAB00)
                                isEngineStarted -> Color(0xFFFF1744)
                                else -> VenusCyan
                            },
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        // Center Text: ENGINE
                        Text(
                            text = "START / STOP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp,
                            color = when {
                                isIgniting -> Color(0xFFFFE082)
                                isEngineStarted -> Color(0xFFFFCDD2)
                                else -> VenusTextPrimary
                            }
                        )

                        // Main Action: ENGINE
                        Text(
                            text = when {
                                isIgniting -> "CRANKING"
                                isEngineStarted -> "ENGINE"
                                else -> "ENGINE"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp,
                            color = when {
                                isIgniting -> Color(0xFFFF9100)
                                isEngineStarted -> Color(0xFFFF1744)
                                else -> VenusCyan
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // High-Performance Core Firing Order Telemetry Bar (1-5-3-6-2-4)
        val firingOrder = listOf(1, 5, 3, 6, 2, 4)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = VenusSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262634)),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "IGNITION CORE",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = VenusTextMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    firingOrder.forEachIndexed { idx, cylNum ->
                        val isFiring = (isEngineStarted || isIgniting) && (idx == activeCylinderIndex)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(width = 16.dp, height = 14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isFiring && isIgniting -> Color(0xFFFF9100)
                                        isFiring -> Color(0xFFFF1744)
                                        isEngineStarted -> Color(0xFF3E1216)
                                        else -> Color(0xFF1E1E28)
                                    }
                                )
                                .border(
                                    0.5.dp,
                                    if (isFiring) Color.White.copy(alpha = 0.7f) else Color.Transparent,
                                    RoundedCornerShape(3.dp)
                                )
                        ) {
                            Text(
                                text = cylNum.toString(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (isFiring) Color.White else VenusTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Real-time RPM readout badge
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = if (isEngineStarted || isIgniting) Color(0xFF2B0A0F) else VenusSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isEngineStarted || isIgniting) Color(0xFFFF5252).copy(alpha = 0.5f) else Color(0xFF333344)
                    )
                ) {
                    Text(
                        text = if (isEngineStarted || isIgniting) "$engineRpm RPM" else "0 RPM",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isEngineStarted || isIgniting) Color(0xFFFF8A80) else VenusTextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Status Ribbon Text
        Text(
            text = when {
                isIgniting -> "IGNITION SEQUENCE ENGAGED • POWERING UP..."
                isEngineStarted -> "IGNITION ARC ONLINE • $engineRpm RPM • TAP TO STOP"
                else -> "TAP IGNITION ARC TO POWER UP V.E.N.U.S"
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.8.sp,
            color = when {
                isIgniting -> Color(0xFFFFB74D)
                isEngineStarted -> Color(0xFFFF8A80)
                else -> VenusCyan
            }
        )
    }
}
