package com.example.ui.screens

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary

@Composable
fun DeviceControlsScreen(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var emailRecipient by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("Update from Bharath") }
    var emailBody by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = VenusLavenderGlow.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Device Controls",
                                tint = VenusLavender,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SYSTEM CONTROLS & UTILITIES",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VenusLavender
                        )
                        Text(
                            text = "Hardware • Files & Folders • Email Assistant • Telemetry",
                            fontSize = 10.sp,
                            color = VenusTextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware Controls Grid
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DEVICE TELEMETRY & HARDWARE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = VenusLavender
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val res = viewModel.actionExecutor.execute("FLASHLIGHT_TOGGLE")
                            feedbackMessage = res.message
                            Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Flashlight", color = VenusMidnightPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val res = viewModel.actionExecutor.execute("CHECK_BATTERY")
                            feedbackMessage = res.message
                            viewModel.speechManager.speak(res.message)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusLavenderGlow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Battery", color = VenusMidnightPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val res = viewModel.actionExecutor.execute("VOLUME_UP")
                            feedbackMessage = res.message
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Vol Up", tint = VenusCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vol +", color = VenusTextPrimary, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val res = viewModel.actionExecutor.execute("VOLUME_DOWN")
                            feedbackMessage = res.message
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeDown, contentDescription = "Vol Down", tint = VenusCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vol -", color = VenusTextPrimary, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val res = viewModel.actionExecutor.execute("VOLUME_MUTE")
                            feedbackMessage = res.message
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeMute, contentDescription = "Mute", tint = VenusCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mute", color = VenusTextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Files & Screenshots
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "STORAGE & SCREEN CAPTURE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = VenusLavender
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.actionExecutor.execute("MANAGE_FILES") },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_file_manager")
                    ) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = "File Manager", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("File Manager", color = VenusMidnightPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.actionExecutor.execute("TAKE_SCREENSHOT") },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusLavenderGlow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_take_screenshot")
                    ) {
                        Icon(imageVector = Icons.Default.Screenshot, contentDescription = "Screenshot", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Screenshot", color = VenusMidnightPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Assistant Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = VenusCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "V.E.N.U.S EMAIL ASSISTANT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = VenusCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = emailRecipient,
                    onValueChange = { emailRecipient = it },
                    label = { Text("Recipient Email", fontSize = 11.sp, color = VenusTextMuted) },
                    placeholder = { Text("e.g. contact@example.com", fontSize = 11.sp, color = VenusTextMuted.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary,
                        focusedBorderColor = VenusLavender,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedContainerColor = VenusSurfaceVariant,
                        unfocusedContainerColor = VenusSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Subject", fontSize = 11.sp, color = VenusTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary,
                        focusedBorderColor = VenusLavender,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedContainerColor = VenusSurfaceVariant,
                        unfocusedContainerColor = VenusSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = emailBody,
                    onValueChange = { emailBody = it },
                    label = { Text("Message Body", fontSize = 11.sp, color = VenusTextMuted) },
                    placeholder = { Text("Type email message...", fontSize = 11.sp, color = VenusTextMuted.copy(alpha = 0.5f)) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary,
                        focusedBorderColor = VenusLavender,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedContainerColor = VenusSurfaceVariant,
                        unfocusedContainerColor = VenusSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val payload = "to=$emailRecipient, subject=$emailSubject, body=$emailBody"
                        val res = viewModel.actionExecutor.execute("SEND_EMAIL", payload)
                        feedbackMessage = res.message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_send_email")
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Send", tint = VenusMidnightPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Draft & Send Email", color = VenusMidnightPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (feedbackMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = VenusSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 12.sp,
                    color = VenusCyan,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
