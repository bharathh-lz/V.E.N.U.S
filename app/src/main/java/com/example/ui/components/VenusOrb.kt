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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AssistantState
import com.example.audio.VenusEmotion
import com.example.ui.theme.EmotionCalmColor
import com.example.ui.theme.EmotionExcitedColor
import com.example.ui.theme.EmotionHappyColor
import com.example.ui.theme.EmotionNeutralColor
import com.example.ui.theme.EmotionSadColor
import com.example.ui.theme.EmotionStressedColor
import com.example.ui.theme.ShieldAmber
import com.example.ui.theme.ShieldBlueDial
import com.example.ui.theme.ShieldBorder
import com.example.ui.theme.ShieldCoreWhite
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.ShieldCyanBright
import com.example.ui.theme.ShieldCyanGlow
import com.example.ui.theme.ShieldDarkBezel
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.VenusTextMuted
import kotlin.math.cos
import kotlin.math.sin

/**
 * S.H.I.E.L.D. OS Arc Reactor / Jarvis HUD Core
 * Faithfully recreating the multi-tier sci-fi arc reactor from the user's reference image:
 * - Concentric outer titanium HUD bezel with calibration ticks and tactical notches
 * - 360-degree calibration dial with fine radial tick marks
 * - Dual counter-rotating gear/notched holographic cyan arc rings
 * - Concentric targeting crosshairs with corner reticle brackets
 * - Triple radial energy gauge arcs modulated by speech audio RMS
 * - 16-fin stator turbine reactor ring
 * - Central luminous plasma core with dynamic digital readout ("41" / core load %)
 */
@Composable
fun VenusOrb(
    assistantState: AssistantState,
    audioRms: Float,
    emotion: VenusEmotion = VenusEmotion.NEUTRAL,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 230.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield_reactor_anim")

    // Smooth breathing core pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Outer gear ring rotation (Counter-Clockwise)
    val outerGearRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (assistantState) {
                    AssistantState.THINKING -> 2500
                    AssistantState.LISTENING_COMMAND -> 6000
                    AssistantState.SPEAKING -> 4000
                    else -> 18000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_gear_rotation"
    )

    // Inner turbine ring rotation (Clockwise)
    val innerTurbineRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (assistantState) {
                    AssistantState.THINKING -> 1800
                    AssistantState.LISTENING_COMMAND -> 4500
                    AssistantState.SPEAKING -> 3200
                    else -> 14000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_turbine_rotation"
    )

    // Dynamic color based on assistant state and emotion
    val activeColor = when (assistantState) {
        AssistantState.THINKING -> ShieldAmber
        AssistantState.ERROR -> Color(0xFFFF1744)
        AssistantState.LISTENING_COMMAND, AssistantState.SPEAKING -> ShieldCyanBright
        else -> when (emotion) {
            VenusEmotion.CALM -> EmotionCalmColor
            VenusEmotion.HAPPY -> EmotionHappyColor
            VenusEmotion.EXCITED -> EmotionExcitedColor
            VenusEmotion.STRESSED -> EmotionStressedColor
            VenusEmotion.SAD -> EmotionSadColor
            VenusEmotion.NEUTRAL -> ShieldCyan
        }
    }

    // Dynamic reactor numeric readout (iconic "41" baseline from reference image)
    val coreLoadPercent = when (assistantState) {
        AssistantState.LISTENING_COMMAND -> (41 + (audioRms * 58f)).toInt().coerceIn(41, 99)
        AssistantState.SPEAKING -> (65 + (audioRms * 34f)).toInt().coerceIn(65, 99)
        AssistantState.THINKING -> 88
        AssistantState.IDLE -> 41
        else -> 41
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .testTag("venus_interactive_orb"),
        contentAlignment = Alignment.Center
    ) {
        // Outer Ethereal Holographic Glow Halo
        Box(
            modifier = Modifier
                .size(sizeDp * 1.05f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            activeColor.copy(alpha = if (assistantState == AssistantState.SPEAKING) 0.35f else 0.22f),
                            ShieldBlueDial.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Arc Reactor Multi-Tier Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) - 4.dp.toPx()

            // 1. Outermost Machined Titanium Bezel Ring
            drawCircle(
                color = ShieldDarkBezel,
                radius = maxRadius,
                center = center,
                style = Stroke(width = 6.dp.toPx())
            )
            drawCircle(
                color = ShieldBorder.copy(alpha = 0.8f),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // 2. 360-Degree Degree Calibration Ticks (Every 5° and 15°)
            drawDegreeTicks(
                center = center,
                radius = maxRadius - 4.dp.toPx(),
                tickColor = ShieldCyan.copy(alpha = 0.5f),
                majorTickColor = ShieldCyanBright
            )

            // 3. Cardinal Reticle Crosshairs with targeting tick marks
            drawTargetingCrosshairs(
                center = center,
                innerRadius = maxRadius * 0.40f,
                outerRadius = maxRadius - 6.dp.toPx(),
                color = ShieldCyan.copy(alpha = 0.35f)
            )

            // 4. Outer Segmented Arc Ring (Cyan notches counter-rotating)
            drawOuterGearTeeth(
                center = center,
                radius = maxRadius * 0.86f,
                rotationDeg = outerGearRotation,
                teethCount = 36,
                color = activeColor
            )

            // 5. Triple Segmented Radial Energy Arcs (Modulated by audio RMS / State)
            val dynamicAudioSweep = (audioRms * 120f).coerceAtLeast(15f)
            drawEnergyGaugeArcs(
                center = center,
                radius = maxRadius * 0.74f,
                color = activeColor,
                audioSweep = dynamicAudioSweep,
                rotationDeg = innerTurbineRotation * 0.5f
            )

            // 6. Inner Turbine Stator Fins (16 Holographic Blades rotating clockwise)
            drawTurbineStatorFins(
                center = center,
                innerRadius = maxRadius * 0.44f,
                outerRadius = maxRadius * 0.62f,
                rotationDeg = innerTurbineRotation,
                bladeCount = 16,
                color = activeColor
            )

            // 7. Concentric Chamber Ring
            drawCircle(
                color = ShieldCyanGlow,
                radius = maxRadius * 0.42f * corePulse,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 8. Core Plasma Gradient Bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ShieldCoreWhite.copy(alpha = 0.95f),
                        activeColor.copy(alpha = 0.75f),
                        ShieldDarkBezel.copy(alpha = 0.85f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 0.40f * corePulse
                ),
                radius = maxRadius * 0.38f * corePulse,
                center = center
            )
        }

        // Central Reactor Core Digital HUD Telemetry ("41" / Load readout)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CORE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ShieldCyanGlow,
                letterSpacing = 1.sp
            )
            Text(
                text = "$coreLoadPercent",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = ShieldCoreWhite,
                letterSpacing = 1.5.sp
            )
            Text(
                text = when (assistantState) {
                    AssistantState.SPEAKING -> "TRANSMIT"
                    AssistantState.LISTENING_COMMAND -> "VOICE REC"
                    AssistantState.THINKING -> "COMPUTE"
                    else -> "ONLINE"
                },
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = activeColor,
                letterSpacing = 0.8.sp
            )
        }
    }
}

/** Draws fine degree marks every 5° and prominent marks every 15° */
private fun DrawScope.drawDegreeTicks(
    center: Offset,
    radius: Float,
    tickColor: Color,
    majorTickColor: Color
) {
    for (deg in 0 until 360 step 5) {
        val isMajor = deg % 15 == 0
        val isCardinal = deg % 90 == 0
        val tickLength = when {
            isCardinal -> 7.dp.toPx()
            isMajor -> 4.5.dp.toPx()
            else -> 2.5.dp.toPx()
        }
        val rad = Math.toRadians(deg.toDouble())
        val startX = center.x + ((radius - tickLength) * cos(rad)).toFloat()
        val startY = center.y + ((radius - tickLength) * sin(rad)).toFloat()
        val endX = center.x + (radius * cos(rad)).toFloat()
        val endY = center.y + (radius * sin(rad)).toFloat()

        drawLine(
            color = if (isMajor) majorTickColor else tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (isMajor) 1.5.dp.toPx() else 0.8.dp.toPx()
        )
    }
}

/** Draws targeting reticles with crosshairs and 4-corner targeting brackets */
private fun DrawScope.drawTargetingCrosshairs(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    color: Color
) {
    val angles = listOf(0.0, 90.0, 180.0, 270.0)
    for (deg in angles) {
        val rad = Math.toRadians(deg)
        val startX = center.x + (innerRadius * cos(rad)).toFloat()
        val startY = center.y + (innerRadius * sin(rad)).toFloat()
        val endX = center.x + (outerRadius * cos(rad)).toFloat()
        val endY = center.y + (outerRadius * sin(rad)).toFloat()

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx()
        )
    }

    // 4 Corner angled HUD brackets (at 45°, 135°, 225°, 315°)
    val cornerAngles = listOf(45.0, 135.0, 225.0, 315.0)
    val bracketRadius = outerRadius * 0.95f
    for (deg in cornerAngles) {
        val rad = Math.toRadians(deg)
        val bx = center.x + (bracketRadius * cos(rad)).toFloat()
        val by = center.y + (bracketRadius * sin(rad)).toFloat()
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(bx, by))
    }
}

/** Draws rotating cyan arc teeth inspired by the outer rotor in the reference image */
private fun DrawScope.drawOuterGearTeeth(
    center: Offset,
    radius: Float,
    rotationDeg: Float,
    teethCount: Int,
    color: Color
) {
    val stepDeg = 360f / teethCount
    for (i in 0 until teethCount) {
        // Leave gaps for tactical sci-fi segmented feel
        if (i % 3 == 0) continue

        val angleDeg = rotationDeg + (i * stepDeg)
        val rad = Math.toRadians(angleDeg.toDouble())
        val innerR = radius - 4.dp.toPx()
        val outerR = radius + 2.dp.toPx()

        val x1 = center.x + (innerR * cos(rad)).toFloat()
        val y1 = center.y + (innerR * sin(rad)).toFloat()
        val x2 = center.x + (outerR * cos(rad)).toFloat()
        val y2 = center.y + (outerR * sin(rad)).toFloat()

        drawLine(
            color = color.copy(alpha = 0.85f),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Square
        )
    }

    // Outer backing track
    drawCircle(
        color = color.copy(alpha = 0.35f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )
}

/** Draws triple segmented radial energy gauge arcs */
private fun DrawScope.drawEnergyGaugeArcs(
    center: Offset,
    radius: Float,
    color: Color,
    audioSweep: Float,
    rotationDeg: Float
) {
    val sweep1 = 60f + (audioSweep * 0.4f)
    val sweep2 = 45f + (audioSweep * 0.3f)
    val sweep3 = 70f + (audioSweep * 0.5f)

    // Arc 1
    drawArc(
        color = color,
        startAngle = rotationDeg + 20f,
        sweepAngle = sweep1,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    // Arc 2
    drawArc(
        color = ShieldCyanGlow,
        startAngle = rotationDeg + 140f,
        sweepAngle = sweep2,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // Arc 3
    drawArc(
        color = color,
        startAngle = rotationDeg + 250f,
        sweepAngle = sweep3,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
}

/** Draws 16 turbine stator blades */
private fun DrawScope.drawTurbineStatorFins(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    rotationDeg: Float,
    bladeCount: Int,
    color: Color
) {
    val step = 360f / bladeCount
    for (i in 0 until bladeCount) {
        val angleDeg = rotationDeg + (i * step)
        val rad1 = Math.toRadians(angleDeg.toDouble())
        val rad2 = Math.toRadians((angleDeg + 12f).toDouble()) // Angled turbine rake

        val x1 = center.x + (innerRadius * cos(rad1)).toFloat()
        val y1 = center.y + (innerRadius * sin(rad1)).toFloat()
        val x2 = center.x + (outerRadius * cos(rad2)).toFloat()
        val y2 = center.y + (outerRadius * sin(rad2)).toFloat()

        drawLine(
            brush = Brush.linearGradient(
                listOf(
                    ShieldCoreWhite.copy(alpha = 0.9f),
                    color.copy(alpha = 0.6f),
                    Color.Transparent
                ),
                start = Offset(x1, y1),
                end = Offset(x2, y2)
            ),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

