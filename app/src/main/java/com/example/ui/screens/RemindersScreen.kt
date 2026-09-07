package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReminderEntity
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusAmber
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusGreen
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersScreen(
    viewModel: VenusViewModel,
    reminders: List<ReminderEntity>,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REMINDERS & AUTOMATIONS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Scheduled alerts and hardware device tasks",
                        fontSize = 12.sp,
                        color = VenusTextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Reminders",
                    tint = VenusAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hardware & System Tasks Test Deck
            Card(
                colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "QUICK AUTOMATED ACTIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickTaskPill("Toggle Torch", Icons.Default.FlashlightOn, VenusAmber) {
                            viewModel.actionExecutor.execute("FLASHLIGHT_TOGGLE")
                        }
                        QuickTaskPill("Volume +", Icons.Default.VolumeUp, VenusCyan) {
                            viewModel.actionExecutor.execute("VOLUME_UP")
                        }
                        QuickTaskPill("Battery Check", Icons.Default.BatteryChargingFull, VenusGreen) {
                            val res = viewModel.actionExecutor.execute("CHECK_BATTERY")
                            viewModel.speechManager.speak(res.message)
                        }
                        QuickTaskPill("Settings", Icons.Default.Settings, VenusViolet) {
                            viewModel.actionExecutor.execute("SETTINGS_MAIN")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Reminders Header
            Text(
                text = "SCHEDULED VOICE REMINDERS (${reminders.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VenusTextMuted,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "No reminders",
                            tint = VenusTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No scheduled reminders",
                            fontSize = 13.sp,
                            color = VenusTextSecondary
                        )
                        Text(
                            text = "Say \"Remind me to drink water\" to add",
                            fontSize = 11.sp,
                            color = VenusTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Add Reminder FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = VenusCyan,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_reminder_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reminder")
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, minutes ->
                viewModel.addManualReminder(title, minutes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun QuickTaskPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = VenusSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, tintColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = VenusTextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderEntity,
    onDelete: () -> Unit
) {
    val timeFormatted = remember(reminder.targetTimeMillis) {
        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(reminder.targetTimeMillis))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (reminder.isCompleted) "Done" else "Pending",
                    tint = if (reminder.isCompleted) VenusGreen else VenusCyan,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = reminder.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VenusTextPrimary
                    )
                    Text(
                        text = "Due: $timeFormatted",
                        fontSize = 11.sp,
                        color = VenusCyanGlow,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = VenusTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, delayMinutes: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VenusSurfaceDark,
        title = {
            Text(
                text = "New Voice Reminder",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = VenusTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title / Task") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_title_input")
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Trigger in (Minutes)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_minutes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val mins = minutesText.toIntOrNull() ?: 10
                        onConfirm(title, mins)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VenusCyan)
            ) {
                Text("Schedule Alert", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VenusTextMuted)
            }
        }
    )
}
