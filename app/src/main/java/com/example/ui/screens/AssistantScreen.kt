package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AssistantState
import com.example.audio.EmotionAnalysisResult
import com.example.audio.VenusEmotion
import com.example.ui.VenusViewModel
import com.example.ui.components.SupercarIgnitionSwitch
import com.example.ui.components.VenusOrb
import com.example.ui.theme.EmotionCalmColor
import com.example.ui.theme.EmotionExcitedColor
import com.example.ui.theme.EmotionHappyColor
import com.example.ui.theme.EmotionNeutralColor
import com.example.ui.theme.EmotionSadColor
import com.example.ui.theme.EmotionStressedColor
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    viewModel: VenusViewModel,
    assistantState: AssistantState,
    audioRms: Float,
    partialTranscript: String,
    currentPrompt: String,
    currentResponse: String,
    currentEmotion: VenusEmotion,
    emotionAnalysis: EmotionAnalysisResult,
    actionFeedback: String?,
    wakeWord: String,
    isContinuousWake: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEngineStarted by viewModel.isEngineStarted.collectAsState()
    val isIgniting by viewModel.isIgniting.collectAsState()
    val engineRpm by viewModel.engineRpm.collectAsState()
    val userDisplayName = viewModel.getUserDisplayName().ifBlank { "Bharath" }

    // Text command input state for typing direct commands to V.E.N.U.S
    var textCommandInput by remember { mutableStateOf("") }

    // Live formatted dynamic time (updates every second)
    val currentTimeString by produceState(initialValue = "") {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        while (true) {
            value = sdf.format(Date()).uppercase()
            delay(1000)
        }
    }

    val emotionColor = when (currentEmotion) {
        VenusEmotion.CALM -> EmotionCalmColor
        VenusEmotion.HAPPY -> EmotionHappyColor
        VenusEmotion.EXCITED -> EmotionExcitedColor
        VenusEmotion.STRESSED -> EmotionStressedColor
        VenusEmotion.SAD -> EmotionSadColor
        VenusEmotion.NEUTRAL -> EmotionNeutralColor
    }

    val infiniteTransition = rememberInfiniteTransition(label = "assistant_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // =========================================================================
        // 1. TOP HEADER: V.E.N.U.S AI name | TIME | SETTINGS
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // V.E.N.U.S AI Name Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(VenusSurfaceDark)
                    .border(1.dp, VenusCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("venus_ai_header_name")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(VenusCyan.copy(alpha = 0.45f), VenusSurfaceVariant)
                            )
                        )
                        .border(1.dp, VenusCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "V.E.N.U.S AI Core Icon",
                        tint = VenusCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "V.E.N.U.S AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            // Time Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VenusSurfaceDark.copy(alpha = 0.7f))
                    .border(1.dp, VenusCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("live_clock_readout")
            ) {
                Text(
                    text = currentTimeString.ifEmpty { "10:00 AM" },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = VenusCyanGlow,
                    letterSpacing = 0.5.sp
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VenusSurfaceDark)
                    .border(1.dp, VenusLavender.copy(alpha = 0.4f), CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings",
                    tint = VenusLavender,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // =========================================================================
        // 2. COMMANDER BHARATH // ACTIVE BANNER
        // =========================================================================
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = VenusSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("commander_status_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676).copy(alpha = pulseAlpha))
                            .border(1.dp, Color(0xFF00E676), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "COMMANDER BHARATH // ACTIVE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = VenusCyan,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = if (isEngineStarted) "CORE ARMED" else "STANDBY",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isEngineStarted) Color(0xFF00E676) else VenusTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // =========================================================================
        // 3. CENTER ORB (Arc Reactor / Holographic Neural Core)
        // =========================================================================
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { viewModel.togglePushToTalk() }
                .testTag("venus_center_orb_container")
        ) {
            VenusOrb(
                assistantState = assistantState,
                audioRms = audioRms,
                emotion = currentEmotion,
                sizeDp = 220.dp,
                modifier = Modifier.testTag("venus_center_orb")
            )
        }

        // Tap to talk / core status label below the orb
        Text(
            text = when (assistantState) {
                AssistantState.LISTENING_COMMAND -> "● LISTENING TO COMMAND..."
                AssistantState.THINKING -> "◈ PROCESSING NEURAL MATRIX..."
                AssistantState.SPEAKING -> "▶ V.E.N.U.S SPEAKING..."
                else -> "TAP ORB TO SPEAK"
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (assistantState == AssistantState.LISTENING_COMMAND) Color(0xFF00E676) else VenusCyan.copy(alpha = 0.7f),
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // =========================================================================
        // 4. IGNITION ARC (Supercar Ignition Switch)
        // =========================================================================
        SupercarIgnitionSwitch(
            isEngineStarted = isEngineStarted,
            isIgniting = isIgniting,
            engineRpm = engineRpm,
            onToggleEngine = { viewModel.toggleEngine(context) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_ignition_arc")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // =========================================================================
        // 4. VENUS REPLY BOX (Interactive Response & Command Console)
        // =========================================================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, VenusCyan.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("venus_reply_box")
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Reply Box Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(emotionColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "V.E.N.U.S REPLY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = VenusLavender,
                            letterSpacing = 1.sp
                        )
                    }

                    // Voice Replay Speaker Button
                    IconButton(
                        onClick = {
                            if (currentResponse.isNotBlank()) {
                                viewModel.speechManager.speak(currentResponse)
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(VenusSurfaceVariant)
                            .testTag("btn_replay_voice")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Response",
                            tint = VenusCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Spoken / Active Prompt Transcript (if present)
                val activePrompt = when {
                    partialTranscript.isNotBlank() -> "Listening: \"$partialTranscript\""
                    currentPrompt.isNotBlank() -> "Command: \"$currentPrompt\""
                    else -> null
                }

                if (activePrompt != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VenusMidnightPurple.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusLavender.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = activePrompt,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VenusLavenderGlow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Main V.E.N.U.S Response Text
                Text(
                    text = currentResponse.ifBlank {
                        "Greetings Commander Bharath. V.E.N.U.S core is online and responsive. Tap the mic or type your command below."
                    },
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = VenusTextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Action Feedback Badge (if any hardware / app action was executed)
                if (!actionFeedback.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0D2818),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                    ) {
                        Text(
                            text = "⚡ $actionFeedback",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Command Input Row (Type or Voice command)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Push-to-Talk Mic Button
                    val isListening = assistantState == AssistantState.LISTENING_COMMAND
                    IconButton(
                        onClick = { viewModel.togglePushToTalk() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFFD50000) else VenusMidnightPurple)
                            .border(
                                1.5.dp,
                                if (isListening) Color.Red else VenusCyan,
                                CircleShape
                            )
                            .testTag("btn_mic_command")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Voice Command",
                            tint = if (isListening) Color.White else VenusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Command Input
                    OutlinedTextField(
                        value = textCommandInput,
                        onValueChange = { textCommandInput = it },
                        placeholder = {
                            Text(
                                "Enter command for V.E.N.U.S...",
                                fontSize = 11.5.sp,
                                color = VenusTextMuted
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (textCommandInput.isNotBlank()) {
                                val cmd = textCommandInput.trim()
                                textCommandInput = ""
                                keyboardController?.hide()
                                viewModel.processUserCommand(cmd)
                            }
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary,
                            cursorColor = VenusCyan,
                            focusedContainerColor = VenusSurfaceVariant,
                            unfocusedContainerColor = VenusSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("input_command_text")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Command Button
                    IconButton(
                        onClick = {
                            if (textCommandInput.isNotBlank()) {
                                val cmd = textCommandInput.trim()
                                textCommandInput = ""
                                keyboardController?.hide()
                                viewModel.processUserCommand(cmd)
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(VenusMidnightPurple)
                            .border(1.5.dp, VenusLavender, CircleShape)
                            .testTag("btn_send_command")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Execute Command",
                            tint = VenusLavender,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Command Suggestions Chips
                val sampleCommands = listOf(
                    "Respond to my command",
                    "What time is it?",
                    "Tell me a joke",
                    "Check battery",
                    "Turn on flashlight"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleCommands) { command ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                            onClick = {
                                viewModel.processUserCommand(command)
                            },
                            modifier = Modifier.testTag("quick_command_${command.take(8)}")
                        ) {
                            Text(
                                text = command,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = VenusCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
