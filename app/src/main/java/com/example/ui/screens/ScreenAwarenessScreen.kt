package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet

@Composable
fun ScreenAwarenessScreen(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    val analysisResult by viewModel.screenAnalysisResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingScreen.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var screenContextInput by remember {
        mutableStateOf(
            "Active Screen: Email Client (Subject: Project Phoenix Q4 Architecture Review)\n" +
            "Sender: Sarah Chen <sarah.chen@techcorp.io>\n" +
            "Message: 'Hi Bharath, please find the attached system metrics. Can you review the Spanner migration timeline by Friday 4 PM?'\n" +
            "Action Buttons: [Reply], [Download Attachment], [Mark as Read]"
        )
    }

    var userGoal by remember { mutableStateOf("Summarize action items and suggest what I should do next.") }
    var copiedFeedback by remember { mutableStateOf(false) }

    val presetContexts = listOf(
        "Email / Work Message" to "Subject: Meeting Rescheduled to 3 PM with Engineering VP. Agenda: Core API Latency.",
        "Code / IDE Snippet" to "fun calculateTelemetry(rms: Float): Float { return sqrt(rms * 100) / 0.5f } // Error: NullPointer on rms",
        "Flight Booking UI" to "Flight AI-204 from JFK to SFO. Departure: 08:30 AM. Gate B12. Seat 14A. Status: On Time.",
        "Article / Lecture Slide" to "Transformer Architecture: Multi-Head Attention mechanisms allow models to jointly attend to information from different representation subspaces."
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VenusSurfaceDark,
                border = BorderStroke(1.dp, VenusCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = VenusViolet.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, VenusCyan),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ScreenSearchDesktop,
                                        contentDescription = "Screen Awareness",
                                        tint = VenusCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "VENUS SCREEN AWARENESS",
                                    color = VenusTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Context Engine • On-Screen OCR • Action Recommendations",
                                    color = VenusCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.speechManager.speak("Screen awareness active. VENUS analyzes what is displayed on your screen and provides instant actions.")
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speak info",
                                tint = VenusLavender
                            )
                        }
                    }
                }
            }
        }

        // Quick Presets
        item {
            Column {
                Text(
                    text = "SAMPLE SCREEN CONTEXTS:",
                    color = VenusTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetContexts.take(2).forEach { (label, content) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VenusSurfaceDark,
                            border = BorderStroke(1.dp, VenusCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { screenContextInput = content }
                        ) {
                            Text(
                                text = label,
                                color = VenusCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetContexts.drop(2).forEach { (label, content) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VenusSurfaceDark,
                            border = BorderStroke(1.dp, VenusCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { screenContextInput = content }
                        ) {
                            Text(
                                text = label,
                                color = VenusCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Active Screen Input & Goal
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = VenusSurfaceDark,
                border = BorderStroke(1.dp, VenusCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📱 Current Screen Content / OCR Stream",
                        color = VenusTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = screenContextInput,
                        onValueChange = { screenContextInput = it },
                        label = { Text("Screen Elements / Text", color = VenusTextMuted) },
                        minLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("screen_context_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userGoal,
                        onValueChange = { userGoal = it },
                        label = { Text("What should VENUS do with this screen?", color = VenusTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.analyzeScreenContent(screenContextInput, userGoal)
                        },
                        enabled = !isAnalyzing && screenContextInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyze_screen_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = VenusPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                color = VenusCyan,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Screen Context...")
                        } else {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = VenusCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze Screen with VENUS", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Analysis Results Card
        if (!analysisResult.isNullOrBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VenusMidnightPurple,
                    border = BorderStroke(1.dp, VenusCyanGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VenusCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SCREEN INTELLIGENCE REPORT",
                                    color = VenusCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(analysisResult ?: ""))
                                        copiedFeedback = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copiedFeedback) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = VenusCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        analysisResult?.let { viewModel.speechManager.speak(it) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak Analysis",
                                        tint = VenusCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = analysisResult ?: "",
                            color = VenusTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
