package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet

@Composable
fun ProjectVenusScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Hero Header
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(VenusViolet.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROJECT V.E.N.U.S",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusCyan,
                            fontFamily = FontFamily.Monospace
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VenusCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan)
                        ) {
                            Text(
                                text = "v2.5 ARCHITECTURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VenusCyan,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Virtual Electronic Networked Utility System",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = VenusTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "An autonomous personal voice intelligence and desktop assistant engineered for total user privacy, empathetic emotion sensing, and seamless device automation.",
                        fontSize = 12.sp,
                        color = VenusTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Pipeline Architecture
        Text(
            text = "SYSTEM PIPELINE & SUBSYSTEMS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VenusTextMuted,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        val architectureSteps = listOf(
            Triple(
                "1. Continuous Acoustic Wake Word Engine",
                "Monitors background audio stream for configurable wake phrase (\"HEY VENUS\"). Utilizes zero-latency offline recognition.",
                Icons.Default.Mic
            ),
            Triple(
                "2. Real-Time Emotion & Cadence Detector",
                "Extracts RMS decibel variance, syllable timing, and lexical sentiment to categorize user state (Calm, Happy, Excited, Stressed, Sad) and adapt AI tone.",
                Icons.Default.Psychology
            ),
            Triple(
                "3. Encrypted Context & Vault Retrieval",
                "Queries local hardware-backed AES-256-GCM memory vault for personalized user facts, preferences, and habits.",
                Icons.Default.Lock
            ),
            Triple(
                "4. Hybrid Dual-Core Intelligence Engine",
                "Routes queries seamlessly: Gemini 3.5 Flash for deep contextual reasoning, or the Local Offline Rule & Math Engine when offline.",
                Icons.Default.Memory
            ),
            Triple(
                "5. Action Dispatcher & Automation Daemon",
                "Executes system commands: app launches, web/YouTube searches, torch control, volume adjustments, battery checks, and scheduled reminders.",
                Icons.Default.Bolt
            ),
            Triple(
                "6. VenusAssist Desktop Floating Overlay",
                "Foreground WindowManager service enabling instant floating access over all active applications.",
                Icons.Default.CastConnected
            ),
            Triple(
                "7. Acoustic Neural TTS Vocalizer",
                "Renders speech output with calibrated pitch, cadence, and emotion-aware modulation.",
                Icons.Default.RecordVoiceOver
            )
        )

        architectureSteps.forEach { (title, desc, icon) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VenusSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = VenusCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = VenusTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy & Cryptography Specs
        Text(
            text = "PRIVACY & LOCAL ENCRYPTION SPECIFICATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VenusTextMuted,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SecuritySpecRow("Encryption Algorithm", "AES / GCM / NoPadding (256-bit)")
                SecuritySpecRow("Initialization Vector", "12-Byte Cryptographically Secure Random IV")
                SecuritySpecRow("Authentication Tag", "128-Bit GCM Authentication Tag")
                SecuritySpecRow("Data Storage", "Local SQLite Room Database (Zero External Sync)")
                SecuritySpecRow("Data Control", "One-Tap Total Vault Wipe Action")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Command Reference Matrix
        Text(
            text = "COMPLETE VOICE COMMAND REFERENCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VenusTextMuted,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                CommandRow("Open <App Name>", "Launches apps like YouTube, Camera, Calculator, Maps, etc.")
                CommandRow("Search Google for <query>", "Opens Google Search in browser with exact query")
                CommandRow("Search YouTube for <topic>", "Searches YouTube video catalog")
                CommandRow("Flashlight On / Off", "Toggles device camera LED torch")
                CommandRow("Volume Up / Down / Mute", "Adjusts system media sound levels")
                CommandRow("Check Battery", "Reads live battery level and charging status")
                CommandRow("What is the time / date?", "Provides precise current timestamp")
                CommandRow("Calculate <expression>", "Evaluates math (e.g. \"45 * 12\", \"100 / 4\")")
                CommandRow("Convert <val> <unit> to <unit>", "Converts miles/km, °C/°F, kg/lbs")
                CommandRow("Remind me to <task>", "Schedules alarm notification alert")
                CommandRow("What is my <profile item>?", "Retrieves encrypted personal memory data")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SecuritySpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = VenusTextSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VenusCyanGlow, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun CommandRow(syntax: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = syntax,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VenusCyan,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = description,
            fontSize = 11.sp,
            color = VenusTextSecondary
        )
    }
}
