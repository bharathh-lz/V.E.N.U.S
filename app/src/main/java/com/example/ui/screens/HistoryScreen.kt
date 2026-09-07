package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.ui.VenusViewModel
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
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: VenusViewModel,
    conversations: List<ConversationEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyBookmarked by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsState()
    val totalCount by viewModel.conversationCount.collectAsState()

    val filteredList = remember(conversations, searchQuery, showOnlyBookmarked) {
        conversations.filter { item ->
            val matchesBookmark = !showOnlyBookmarked || item.isBookmarked
            val matchesQuery = searchQuery.isBlank() ||
                item.userPrompt.contains(searchQuery, ignoreCase = true) ||
                item.venusResponse.contains(searchQuery, ignoreCase = true) ||
                item.emotion.contains(searchQuery, ignoreCase = true) ||
                (item.actionExecuted?.contains(searchQuery, ignoreCase = true) == true)
            matchesBookmark && matchesQuery
        }
    }

    val bookmarkedCount = remember(conversations) {
        conversations.count { it.isBookmarked }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "V.E.N.U.S CHAT MEMORY",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Room SQLite Encrypted Local Persistence",
                    fontSize = 11.sp,
                    color = VenusTextSecondary
                )
            }

            if (conversations.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_clear_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear All History",
                        tint = VenusTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Personalization & Persistence HUD Metric Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Room Storage",
                        tint = VenusCyanGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "USER: $userName",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = VenusCyan.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "$totalCount LOGGED EXCHANGES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Filter Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search dialogue memory, actions, or emotions...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = VenusCyan,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VenusCyan,
                unfocusedBorderColor = VenusCardBorder,
                focusedTextColor = VenusTextPrimary,
                unfocusedTextColor = VenusTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_history_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Tabs: All vs Bookmarked
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showOnlyBookmarked,
                onClick = { showOnlyBookmarked = false },
                label = { Text("All Logs (${conversations.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VenusCyan.copy(alpha = 0.2f),
                    selectedLabelColor = VenusCyan,
                    containerColor = VenusSurfaceDark,
                    labelColor = VenusTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = !showOnlyBookmarked,
                    borderColor = if (!showOnlyBookmarked) VenusCyan else VenusCardBorder
                ),
                modifier = Modifier.testTag("chip_all_history")
            )

            FilterChip(
                selected = showOnlyBookmarked,
                onClick = { showOnlyBookmarked = true },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorites",
                        tint = if (showOnlyBookmarked) VenusCyan else VenusTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                },
                label = { Text("Bookmarked ($bookmarkedCount)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VenusCyan.copy(alpha = 0.2f),
                    selectedLabelColor = VenusCyan,
                    containerColor = VenusSurfaceDark,
                    labelColor = VenusTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showOnlyBookmarked,
                    borderColor = if (showOnlyBookmarked) VenusCyan else VenusCardBorder
                ),
                modifier = Modifier.testTag("chip_bookmarked_history")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Empty History",
                        tint = VenusTextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isBlank()) {
                            if (showOnlyBookmarked) "No bookmarked conversations yet" else "No conversation history saved in Room"
                        } else {
                            "No matching memory records"
                        },
                        fontSize = 13.sp,
                        color = VenusTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    ConversationHistoryCard(
                        item = item,
                        userName = userName,
                        onReplay = { viewModel.speechManager.speak(item.venusResponse) },
                        onToggleBookmark = { viewModel.toggleConversationBookmark(item.id, !item.isBookmarked) },
                        onDelete = {
                            viewModel.deleteConversation(item.id)
                            Toast.makeText(context, "Log deleted from Room DB", Toast.LENGTH_SHORT).show()
                        },
                        onCopy = {
                            val textToCopy = "USER: ${item.userPrompt}\nVENUS: ${item.venusResponse}"
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            Toast.makeText(context, "Copied dialogue to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear All Dialogue History?",
                    color = VenusTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently purge all recorded conversations from the local Room database. This action cannot be undone.",
                    color = VenusTextSecondary,
                    fontSize = 13.sp
                )
            },
            containerColor = VenusSurfaceDark,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearConversationHistory()
                        showClearDialog = false
                        Toast.makeText(context, "All history purged from Room DB", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = VenusCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = VenusTextMuted)
                }
            }
        )
    }
}

@Composable
fun ConversationHistoryCard(
    item: ConversationEntity,
    userName: String,
    onReplay: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val dateStr = remember(item.timestamp) {
        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    val emotionColor = when (item.emotion.uppercase(Locale.ROOT)) {
        "CALM" -> EmotionCalmColor
        "HAPPY" -> EmotionHappyColor
        "EXCITED" -> EmotionExcitedColor
        "STRESSED" -> EmotionStressedColor
        "SAD" -> EmotionSadColor
        else -> EmotionNeutralColor
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isBookmarked) VenusCyan.copy(alpha = 0.8f) else VenusCardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Timestamp + Mode + Emotion + Bookmark Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = VenusTextMuted,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Mode Tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = VenusSurfaceVariant
                    ) {
                        Text(
                            text = item.mode,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.mode.contains("ONLINE")) VenusCyan else VenusTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Emotion Tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = emotionColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.emotion,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = emotionColor,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (item.isBookmarked) VenusCyan else VenusTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Prompt
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "$userName: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusCyan,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = item.userPrompt,
                    fontSize = 13.sp,
                    color = VenusTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Venus Response
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "V.E.N.U.S: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusViolet,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = item.venusResponse,
                    fontSize = 13.sp,
                    color = VenusTextPrimary,
                    lineHeight = 18.sp
                )
            }

            // Action tag if executed
            item.actionExecuted?.let { action ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Action",
                        tint = VenusCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = action,
                        fontSize = 10.sp,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Action Icons: Copy, Replay, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Dialogue",
                        tint = VenusTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onReplay,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voice Replay",
                        tint = VenusCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Single Item",
                        tint = VenusTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
