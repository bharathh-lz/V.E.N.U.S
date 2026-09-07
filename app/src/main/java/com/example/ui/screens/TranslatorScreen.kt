package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TranslationEntity
import com.example.ui.VenusViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
    viewModel: VenusViewModel,
    sourceLanguage: String,
    targetLanguage: String,
    sourceText: String,
    translatedResult: String,
    isTranslating: Boolean,
    translationHistory: List<TranslationEntity>,
    modifier: Modifier = Modifier
) {
    val languages = listOf(
        "English", "Spanish", "French", "German", "Japanese",
        "Chinese", "Hindi", "Arabic", "Russian", "Portuguese",
        "Italian", "Korean", "Dutch", "Turkish", "Swedish"
    )

    var manualInputText by remember { mutableStateOf("") }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var targetDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "REAL-TIME VOICE TRANSLATOR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusCyan,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Speak in one language, hear in another",
                    fontSize = 12.sp,
                    color = VenusTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Translate",
                tint = VenusViolet,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selectors Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source Language Selector
            ExposedDropdownMenuBox(
                expanded = sourceDropdownExpanded,
                onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = sourceLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From", fontSize = 11.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceDropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = sourceDropdownExpanded,
                    onDismissRequest = { sourceDropdownExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                viewModel.updateLanguages(lang, targetLanguage)
                                sourceDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Swap Button
            IconButton(
                onClick = {
                    viewModel.updateLanguages(targetLanguage, sourceLanguage)
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(VenusSurfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap Languages",
                    tint = VenusCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Target Language Selector
            ExposedDropdownMenuBox(
                expanded = targetDropdownExpanded,
                onExpandedChange = { targetDropdownExpanded = !targetDropdownExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = targetLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To", fontSize = 11.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetDropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusViolet,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = targetDropdownExpanded,
                    onDismissRequest = { targetDropdownExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                viewModel.updateLanguages(sourceLanguage, lang)
                                targetDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input & Translation Live Cards
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Input TextField + Speech Trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualInputText,
                        onValueChange = { manualInputText = it },
                        placeholder = { Text("Type or tap microphone to speak in $sourceLanguage...", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("translate_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (manualInputText.isNotBlank()) {
                                viewModel.translateVoiceInput(manualInputText)
                                manualInputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(VenusCyan, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Translate Input",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast voice translator buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            viewModel.translateVoiceInput("Hello, how are you today?")
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = VenusSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "\"Hello, how are you?\"",
                            fontSize = 11.sp,
                            color = VenusTextSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Surface(
                        onClick = {
                            viewModel.translateVoiceInput("Where is the nearest train station?")
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = VenusSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "\"Where is train station?\"",
                            fontSize = 11.sp,
                            color = VenusTextSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Translated Result Section
                if (isTranslating) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = VenusViolet
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Translating speech and synthesizing $targetLanguage audio...",
                            fontSize = 12.sp,
                            color = VenusTextSecondary
                        )
                    }
                } else if (translatedResult.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VenusSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusViolet.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$targetLanguage Translation:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VenusViolet,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = translatedResult,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VenusTextPrimary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.speechManager.speak(translatedResult) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(VenusViolet.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Translation",
                                    tint = VenusViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Translation History Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TRANSLATION LOGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VenusTextMuted,
                fontFamily = FontFamily.Monospace
            )

            if (translationHistory.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearTranslationHistory() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = VenusTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (translationHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No translation records yet",
                    fontSize = 12.sp,
                    color = VenusTextMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(translationHistory, key = { it.id }) { item ->
                    TranslationHistoryCard(
                        item = item,
                        onSpeak = { viewModel.speechManager.speak(item.translatedText) }
                    )
                }
            }
        }
    }
}

@Composable
fun TranslationHistoryCard(
    item: TranslationEntity,
    onSpeak: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.sourceLanguage,
                        fontSize = 10.sp,
                        color = VenusCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "to",
                        tint = VenusTextMuted,
                        modifier = Modifier
                            .size(10.dp)
                            .padding(horizontal = 2.dp)
                    )
                    Text(
                        text = item.targetLanguage,
                        fontSize = 10.sp,
                        color = VenusViolet,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.sourceText,
                    fontSize = 12.sp,
                    color = VenusTextSecondary
                )
                Text(
                    text = item.translatedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VenusTextPrimary
                )
            }

            IconButton(
                onClick = onSpeak,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak",
                    tint = VenusCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
