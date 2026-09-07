package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiOff
import com.example.audio.VenusEngineSoundGenerator
import com.example.audio.VenusLanguageMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.TranslationEntity
import com.example.ui.UserVaultItem
import com.example.ui.VenusViewModel
import com.example.ui.theme.VenusBgDark
import com.example.ui.theme.VenusCardBorder
import com.example.ui.theme.VenusCyan
import com.example.ui.theme.VenusCyanGlow
import com.example.ui.theme.VenusLavender
import com.example.ui.theme.VenusLavenderGlow
import android.widget.Toast
import com.example.ui.theme.VenusMidnightPurple
import com.example.ui.theme.VenusPurple
import com.example.ui.theme.VenusSurfaceDark
import com.example.ui.theme.VenusSurfaceVariant
import com.example.ui.theme.VenusTextMuted
import com.example.ui.theme.VenusTextPrimary
import com.example.ui.theme.VenusTextSecondary
import com.example.ui.theme.VenusViolet
import java.util.Locale

import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Palette
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.audio.VenusContact
import com.example.audio.VenusContactManager
import com.example.audio.VenusVoiceBiometrics
import com.example.audio.VoiceProfile
import com.example.ui.components.HomepageCustomizationContent

enum class SettingsSection(val title: String, val icon: ImageVector, val tag: String) {
    HOMEPAGE_CUSTOMIZATION("Homepage & Theme", Icons.Default.Palette, "sec_homepage"),
    VOICE_AUDIO("Voice & Engine", Icons.Default.GraphicEq, "sec_voice"),
    STUDY_ASSISTANT("Study Assistant", Icons.Default.School, "sec_study"),
    SMART_NOTES("Smart Notes", Icons.Default.EditNote, "sec_smart_notes"),
    SCREEN_AWARENESS("Screen Radar", Icons.Default.Visibility, "sec_screen"),
    CONTACTS("Phone Contacts", Icons.Default.Phone, "sec_contacts"),
    VOICE_ID("Voice ID: Bharath", Icons.Default.RecordVoiceOver, "sec_voice_id"),
    VISION_SCANNER("Vision & Face AI", Icons.Default.Face, "sec_vision"),
    CODE_STUDIO("Code Studio", Icons.Default.Code, "sec_code"),
    DOC_SUMMARIZER("Doc Summarizer", Icons.Default.Description, "sec_doc"),
    ENTERTAINMENT("Movies & Music", Icons.Default.Movie, "sec_entertainment"),
    GAMES("Games & Trivia", Icons.Default.SportsEsports, "sec_games"),
    SYSTEM_CONTROLS("Device & Files", Icons.Default.Settings, "sec_device"),
    DATA_FEED("Feed Data", Icons.Default.Person, "sec_data_feed"),
    TRANSLATOR("Translate", Icons.Default.Translate, "sec_translate"),
    TASKS("Tasks", Icons.Default.Alarm, "sec_tasks"),
    MEMORY("Memory Vault", Icons.Default.Psychology, "sec_memory"),
    ARCHITECTURE("Architecture", Icons.Default.AutoAwesome, "sec_arch")
}

@Composable
fun SettingsScreen(
    viewModel: VenusViewModel,
    wakeWord: String,
    isContinuousWake: Boolean,
    speechPitch: Float,
    speechRate: Float,
    autoSpeak: Boolean,
    forceOffline: Boolean,
    isOverlayActive: Boolean,
    userDataItems: List<UserVaultItem>,
    conversations: List<ConversationEntity>,
    reminders: List<ReminderEntity>,
    translations: List<TranslationEntity>,
    sourceLanguage: String,
    targetLanguage: String,
    sourceText: String,
    translatedResult: String,
    isTranslating: Boolean,
    onNavigateBack: () -> Unit,
    initialSection: SettingsSection = SettingsSection.VOICE_AUDIO,
    modifier: Modifier = Modifier
) {
    var currentSection by remember { mutableStateOf(initialSection) }
    val tabScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VenusBgDark)
    ) {
        // Top Navigation Header
        Surface(
            color = VenusSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = onNavigateBack,
                            shape = CircleShape,
                            color = VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("btn_back_to_assistant")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Assistant",
                                    tint = VenusLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "CONTROL HUB",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                color = VenusLavender
                            )
                            Text(
                                text = "SETTINGS • MEMORY • TRANSLATE • TASKS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = VenusTextMuted
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = VenusLavenderGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Section Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsSection.values().forEach { section ->
                        val isSelected = currentSection == section
                        Surface(
                            onClick = { currentSection = section },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) VenusLavenderGlow else VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) VenusLavender else VenusCardBorder
                            ),
                            modifier = Modifier.testTag(section.tag)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = section.icon,
                                    contentDescription = section.title,
                                    tint = if (isSelected) VenusMidnightPurple else VenusLavender,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = section.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) VenusMidnightPurple else VenusTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (currentSection) {
                SettingsSection.HOMEPAGE_CUSTOMIZATION -> {
                    HomepageCustomizationContent(
                        viewModel = viewModel,
                        onClose = onNavigateBack
                    )
                }
                SettingsSection.VOICE_AUDIO -> {
                    VoiceAndSystemSettingsContent(
                        viewModel = viewModel,
                        wakeWord = wakeWord,
                        isContinuousWake = isContinuousWake,
                        speechPitch = speechPitch,
                        speechRate = speechRate,
                        autoSpeak = autoSpeak,
                        forceOffline = forceOffline,
                        isOverlayActive = isOverlayActive
                    )
                }
                SettingsSection.STUDY_ASSISTANT -> {
                    StudyAssistantScreen(viewModel = viewModel)
                }
                SettingsSection.SMART_NOTES -> {
                    SmartNotesScreen(viewModel = viewModel)
                }
                SettingsSection.SCREEN_AWARENESS -> {
                    ScreenAwarenessScreen(viewModel = viewModel)
                }
                SettingsSection.CONTACTS -> {
                    ContactsSettingsContent(viewModel = viewModel)
                }
                SettingsSection.VOICE_ID -> {
                    VoiceIdSettingsContent(viewModel = viewModel)
                }
                SettingsSection.VISION_SCANNER -> {
                    VisionScannerScreen(viewModel = viewModel)
                }
                SettingsSection.CODE_STUDIO -> {
                    CodeStudioScreen(viewModel = viewModel)
                }
                SettingsSection.DOC_SUMMARIZER -> {
                    DocSummarizerScreen(viewModel = viewModel)
                }
                SettingsSection.ENTERTAINMENT -> {
                    EntertainmentScreen(viewModel = viewModel)
                }
                SettingsSection.GAMES -> {
                    GamesScreen(viewModel = viewModel)
                }
                SettingsSection.SYSTEM_CONTROLS -> {
                    DeviceControlsScreen(viewModel = viewModel)
                }
                SettingsSection.DATA_FEED -> {
                    DataFeedScreen(
                        viewModel = viewModel,
                        userDataItems = userDataItems,
                        onNavigateToAssistant = onNavigateBack
                    )
                }
                SettingsSection.TRANSLATOR -> {
                    TranslatorScreen(
                        viewModel = viewModel,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        sourceText = sourceText,
                        translatedResult = translatedResult,
                        isTranslating = isTranslating,
                        translationHistory = translations
                    )
                }
                SettingsSection.TASKS -> {
                    RemindersScreen(
                        viewModel = viewModel,
                        reminders = reminders
                    )
                }
                SettingsSection.MEMORY -> {
                    HistoryScreen(
                        viewModel = viewModel,
                        conversations = conversations
                    )
                }
                SettingsSection.ARCHITECTURE -> {
                    ProjectVenusScreen()
                }
            }
        }
    }
}

@Composable
fun VoiceAndSystemSettingsContent(
    viewModel: VenusViewModel,
    wakeWord: String,
    isContinuousWake: Boolean,
    speechPitch: Float,
    speechRate: Float,
    autoSpeak: Boolean,
    forceOffline: Boolean,
    isOverlayActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val availableVoices = viewModel.availableVoices
    val availableSystemVoices by viewModel.availableSystemVoices.collectAsState()
    val currentLangMode by viewModel.languageMode.collectAsState()
    val currentResponseVol by viewModel.responseVolume.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val personalityTone by viewModel.personalityTone.collectAsState()
    val customInstructions by viewModel.customInstructions.collectAsState()

    var localUserName by remember(userName) { mutableStateOf(userName) }
    var localPersonalityTone by remember(personalityTone) { mutableStateOf(personalityTone) }
    var localDirectives by remember(customInstructions) { mutableStateOf(customInstructions) }

    var localWakeWord by remember(wakeWord) { mutableStateOf(wakeWord) }
    var localContinuousWake by remember(isContinuousWake) { mutableStateOf(isContinuousWake) }
    var localPitch by remember(selectedVoice) { mutableFloatStateOf(selectedVoice.pitch) }
    var localRate by remember(selectedVoice) { mutableFloatStateOf(selectedVoice.rate) }
    var localAutoSpeak by remember(autoSpeak) { mutableStateOf(autoSpeak) }
    var localForceOffline by remember(forceOffline) { mutableStateOf(forceOffline) }
    var localResponseVol by remember(currentResponseVol) { mutableFloatStateOf(currentResponseVol) }

    fun syncSettings() {
        viewModel.updateSettings(
            wakeWord = localWakeWord,
            continuous = localContinuousWake,
            pitch = localPitch,
            rate = localRate,
            autoSpeak = localAutoSpeak,
            forceOffline = localForceOffline,
            mode = currentLangMode,
            volume = localResponseVol
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Personalized V.E.N.U.S AI Experience & Room Database Storage
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_user_personalization")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Persona",
                            tint = VenusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Personalized AI Profile",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VenusCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan)
                    ) {
                        Text(
                            text = "ROOM DB PERSISTED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusCyan,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "V.E.N.U.S stores your name, personality tone, custom directives, and full conversation history locally in encrypted Room SQLite for a personalized, continuous assistant experience.",
                    fontSize = 11.sp,
                    color = VenusTextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // User Name Field
                Text(
                    text = "USER NAME / CALLSIGN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusCyan,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = localUserName,
                    onValueChange = { localUserName = it },
                    placeholder = { Text("Enter your name (e.g. Bharath)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_user_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Personality Tone selector & Presets
                Text(
                    text = "ASSISTANT PERSONA & TONE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusCyan,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = localPersonalityTone,
                    onValueChange = { localPersonalityTone = it },
                    placeholder = { Text("Tone style for V.E.N.U.S responses", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_personality_tone")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Presets
                Text(
                    text = "Quick Presets:",
                    fontSize = 10.sp,
                    color = VenusTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                val presets = listOf(
                    "Sophisticated & Direct",
                    "Cockpit JARVIS HUD",
                    "Empathetic & Friendly",
                    "Tactical Military AI"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { preset ->
                        Surface(
                            onClick = { localPersonalityTone = preset },
                            shape = RoundedCornerShape(8.dp),
                            color = if (localPersonalityTone == preset) VenusCyan.copy(alpha = 0.2f) else VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (localPersonalityTone == preset) VenusCyan else VenusCardBorder
                            )
                        ) {
                            Text(
                                text = preset,
                                fontSize = 10.sp,
                                color = if (localPersonalityTone == preset) VenusCyan else VenusTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Directives for Gemini
                Text(
                    text = "CUSTOM OPERATING DIRECTIVES FOR GEMINI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VenusCyan,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = localDirectives,
                    onValueChange = { localDirectives = it },
                    placeholder = { Text("e.g., Always address me as Boss, keep answers concise with bullet points", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_directives")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.updateUserPersona(localUserName, localPersonalityTone, localDirectives)
                        Toast.makeText(context, "Saved to Room DB: Personalized Profile Active", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_save_persona_room")
                ) {
                    Text(
                        text = "SAVE PROFILE TO ROOM PERSISTENCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 0. Language Configuration: Tamil & English Support
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_language_configuration")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = VenusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tamil & English Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VenusCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan)
                    ) {
                        Text(
                            text = "தமிழ் TTS + STT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configure VENUS speech-to-text recognition and text-to-speech engine to speak, understand, and respond in Tamil.",
                    fontSize = 11.sp,
                    color = VenusTextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Language options: Bilingual Auto, Tamil Dedicated, English Dedicated
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val langOptions = listOf(
                        Triple(
                            VenusLanguageMode.BILINGUAL_AUTO,
                            "Bilingual Auto (English + தமிழ்)",
                            "Listens in both languages and responds naturally in Tamil or English."
                        ),
                        Triple(
                            VenusLanguageMode.TAMIL_ONLY,
                            "Tamil Dedicated (தமிழ் மட்டும்)",
                            "Prioritizes Tamil speech recognition and speaks back primarily in Tamil."
                        ),
                        Triple(
                            VenusLanguageMode.ENGLISH_ONLY,
                            "English Dedicated",
                            "Operates with standard English STT and TTS engines."
                        )
                    )

                    langOptions.forEach { (modeOption, title, desc) ->
                        val isModeSelected = currentLangMode == modeOption
                        Surface(
                            onClick = {
                                viewModel.setLanguageMode(modeOption)
                                syncSettings()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isModeSelected) VenusSurfaceVariant else VenusSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isModeSelected) 1.5.dp else 1.dp,
                                if (isModeSelected) VenusCyan else VenusCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isModeSelected,
                                    onClick = {
                                        viewModel.setLanguageMode(modeOption)
                                        syncSettings()
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = VenusCyan,
                                        unselectedColor = VenusTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isModeSelected) VenusCyan else VenusTextPrimary
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 10.sp,
                                        color = VenusTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Language Test Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.speechManager.speak("வணக்கம்! நான் வீனஸ். உங்கள் பல்பணி AI உதவியாளர் தயார்.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("test_tamil_speech_btn")
                    ) {
                        Text(
                            text = "வணக்கம் வீனஸ்",
                            color = VenusMidnightPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.speechManager.speak("Hello! I am VENUS. Multi-tasking bilingual AI ready.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusLavender),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("test_english_speech_btn")
                    ) {
                        Text(
                            text = "Hello VENUS",
                            color = VenusLavender,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 0.5 Response Sound Volume Slider (Reduced Response Sound Volume)
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_response_sound_volume")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeDown,
                            contentDescription = "Volume",
                            tint = VenusLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Response Sound Volume",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                    }

                    Text(
                        text = "${(localResponseVol * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusLavender
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Controls action confirmation sounds and TTS speech volume. Reduced by default for soft listening comfort.",
                    fontSize = 11.sp,
                    color = VenusTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = localResponseVol,
                    onValueChange = {
                        localResponseVol = it
                        viewModel.setResponseVolume(it)
                        syncSettings()
                    },
                    valueRange = 0.10f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = VenusLavender,
                        activeTrackColor = VenusLavender,
                        inactiveTrackColor = VenusSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("response_sound_volume_slider")
                )

                // Volume Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair("Soft (40%)", 0.40f),
                        Pair("Comfort (60%)", 0.60f),
                        Pair("Normal (80%)", 0.80f),
                        Pair("Max (100%)", 1.0f)
                    ).forEach { (label, volValue) ->
                        val isPreset = kotlin.math.abs(localResponseVol - volValue) < 0.05f
                        Surface(
                            onClick = {
                                localResponseVol = volValue
                                viewModel.setResponseVolume(volValue)
                                syncSettings()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPreset) VenusLavenderGlow else VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPreset) VenusLavender else VenusCardBorder
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isPreset) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPreset) VenusMidnightPurple else VenusTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            VenusEngineSoundGenerator.playEngineStartSound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Engine Start Sound",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80)
                        )
                    }

                    Button(
                        onClick = {
                            VenusEngineSoundGenerator.playEngineStopSound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Engine Spin-Down",
                            fontSize = 10.sp,
                            color = VenusTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Wake Word Customization
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Wake Word",
                        tint = VenusLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wake Word Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = localWakeWord,
                    onValueChange = {
                        localWakeWord = it.uppercase(Locale.ROOT)
                        syncSettings()
                    },
                    label = { Text("Active Wake Word (Default: HEY VENUS)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusLavender,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wake_word_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Wake Word Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("HEY VENUS", "VENUS", "OK VENUS", "ஹே வீனஸ்").forEach { preset ->
                        val isPresetActive = localWakeWord.equals(preset, ignoreCase = true)
                        Surface(
                            onClick = {
                                localWakeWord = preset
                                syncSettings()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPresetActive) VenusLavenderGlow else VenusSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPresetActive) VenusLavender else VenusCardBorder
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 5.dp)
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 10.sp,
                                    fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPresetActive) VenusMidnightPurple else VenusTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Always Listening Daemon",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = VenusTextPrimary
                        )
                        Text(
                            text = "Listens continuously for \"$localWakeWord\" in background",
                            fontSize = 11.sp,
                            color = VenusTextSecondary
                        )
                    }

                    Switch(
                        checked = localContinuousWake,
                        onCheckedChange = {
                            localContinuousWake = it
                            syncSettings()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VenusMidnightPurple,
                            checkedTrackColor = VenusLavender,
                            uncheckedTrackColor = VenusSurfaceVariant
                        ),
                        modifier = Modifier.testTag("continuous_listening_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. VenusAssist Floating Desktop Overlay
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusPurple.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CastConnected,
                            contentDescription = "VenusAssist",
                            tint = VenusLavender,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VenusAssist Desktop Overlay",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VenusTextPrimary
                            )
                            Text(
                                text = "Floating holographic AI widget accessible over any app",
                                fontSize = 11.sp,
                                color = VenusTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isOverlayActive,
                        onCheckedChange = { active ->
                            viewModel.toggleOverlayService(context, active)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VenusMidnightPurple,
                            checkedTrackColor = VenusLavender,
                            uncheckedTrackColor = VenusSurfaceVariant
                        ),
                        modifier = Modifier.testTag("overlay_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Neural Voice Selection (8 Distinct Voices)
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusLavenderGlow),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("voice_selection_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Voices",
                            tint = VenusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VENUS Voice Persona Selection",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VenusCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan)
                    ) {
                        Text(
                            text = "8 VOICES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a synthesized voice persona. Tapping a voice or the Speak button will say \"Hello! I am VENUS\".",
                    fontSize = 12.sp,
                    color = VenusTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Quick Test Button: Speaks "Hello! I am VENUS"
                Button(
                    onClick = { viewModel.speakVenusGreeting() },
                    colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("speak_hello_venus_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak Greeting",
                        tint = VenusMidnightPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Say \"Hello! I am VENUS\" (Active Voice)",
                        color = VenusMidnightPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Voice Personas List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableVoices.forEach { voice ->
                        val isSelected = voice.id == selectedVoice.id
                        Surface(
                            onClick = {
                                localPitch = voice.pitch
                                localRate = voice.rate
                                viewModel.selectVoicePreset(voice.id, preview = true)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) VenusSurfaceVariant else VenusSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) VenusCyan else VenusCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voice_option_${voice.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        localPitch = voice.pitch
                                        localRate = voice.rate
                                        viewModel.selectVoicePreset(voice.id, preview = true)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = VenusCyan,
                                        unselectedColor = VenusTextMuted
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = voice.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) VenusCyan else VenusTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) VenusCyan.copy(alpha = 0.2f) else VenusSurfaceVariant
                                        ) {
                                            Text(
                                                text = "${voice.personaTag} • ${voice.pitch}x",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isSelected) VenusCyan else VenusTextMuted,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = voice.description,
                                        fontSize = 11.sp,
                                        color = VenusTextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = {
                                        localPitch = voice.pitch
                                        localRate = voice.rate
                                        viewModel.selectVoicePreset(voice.id, preview = true)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Preview ${voice.name}",
                                        tint = if (isSelected) VenusCyan else VenusLavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // System Voices List (if detected on device)
                if (availableSystemVoices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "DETECTED SYSTEM TTS VOICES (${availableSystemVoices.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = VenusLavender
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        availableSystemVoices.take(5).forEach { sysVoiceName ->
                            Surface(
                                onClick = { viewModel.setSystemVoice(sysVoiceName) },
                                shape = RoundedCornerShape(8.dp),
                                color = VenusSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sysVoiceName,
                                        fontSize = 11.sp,
                                        color = VenusTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Select System Voice",
                                        tint = VenusLavender,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Voice Acoustic Fine-Tuning (Pitch & Rate)
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = "Voice Synthesis",
                        tint = VenusLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vocal Pitch & Cadence Fine-Tuning",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pitch Slider
                Text(
                    text = "Vocal Pitch: ${String.format(Locale.US, "%.2fx", localPitch)}",
                    fontSize = 12.sp,
                    color = VenusLavender,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = localPitch,
                    onValueChange = {
                        localPitch = it
                        syncSettings()
                    },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = VenusLavender,
                        activeTrackColor = VenusLavender,
                        inactiveTrackColor = VenusSurfaceVariant
                    ),
                    modifier = Modifier.testTag("pitch_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Speech Rate Slider
                Text(
                    text = "Speech Rate: ${String.format(Locale.US, "%.2fx", localRate)}",
                    fontSize = 12.sp,
                    color = VenusLavender,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = localRate,
                    onValueChange = {
                        localRate = it
                        syncSettings()
                    },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = VenusLavender,
                        activeTrackColor = VenusLavender,
                        inactiveTrackColor = VenusSurfaceVariant
                    ),
                    modifier = Modifier.testTag("rate_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Test Voice Button
                Button(
                    onClick = {
                        viewModel.speechManager.speak("Hello! I am VENUS. Your AI assistant is configured and operating with high precision.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VenusLavender),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_voice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Test Voice",
                        tint = VenusLavender,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Synthesized Speech", color = VenusLavender, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Autonomous Spoken Responses & Offline Mode
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Auto-speak toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Speak Audio Responses",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = VenusTextPrimary
                        )
                        Text(
                            text = "Vocalize answers aloud automatically",
                            fontSize = 11.sp,
                            color = VenusTextSecondary
                        )
                    }

                    Switch(
                        checked = localAutoSpeak,
                        onCheckedChange = {
                            localAutoSpeak = it
                            syncSettings()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VenusMidnightPurple,
                            checkedTrackColor = VenusLavender,
                            uncheckedTrackColor = VenusSurfaceVariant
                        ),
                        modifier = Modifier.testTag("auto_speak_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Force Offline toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Force Air-Gapped / Offline Engine",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = VenusTextPrimary
                        )
                        Text(
                            text = "Strict zero-cloud local intent parsing and rule-matching",
                            fontSize = 11.sp,
                            color = VenusTextSecondary
                        )
                    }

                    Switch(
                        checked = localForceOffline,
                        onCheckedChange = {
                            localForceOffline = it
                            syncSettings()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VenusMidnightPurple,
                            checkedTrackColor = VenusLavender,
                            uncheckedTrackColor = VenusSurfaceVariant
                        ),
                        modifier = Modifier.testTag("force_offline_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ContactsSettingsContent(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var contactsList by remember { mutableStateOf<List<VenusContact>>(emptyList()) }
    var directNumberInput by remember { mutableStateOf("") }
    var hasCheckedContacts by remember { mutableStateOf(false) }

    fun refreshContacts() {
        contactsList = VenusContactManager.getAllContacts(context)
        hasCheckedContacts = true
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        refreshContacts()
    }

    val filteredContacts = contactsList.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.phoneNumber.contains(searchQuery)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Direct Call Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Direct Call",
                        tint = VenusCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Contact Calling",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Say \"Call Mom\", \"Dial Rahul\", or enter a name/number below:",
                    fontSize = 12.sp,
                    color = VenusTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = directNumberInput,
                        onValueChange = { directNumberInput = it },
                        placeholder = { Text("Contact name or phone number", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VenusCyan,
                            unfocusedBorderColor = VenusCardBorder,
                            focusedTextColor = VenusTextPrimary,
                            unfocusedTextColor = VenusTextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_contact_dial_input")
                    )

                    Button(
                        onClick = {
                            if (directNumberInput.isNotBlank()) {
                                viewModel.callContact(directNumberInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("settings_dial_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = VenusMidnightPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", color = VenusMidnightPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Device Phonebook List
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Contacts,
                            contentDescription = "Contacts Book",
                            tint = VenusLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phonebook Contacts (${filteredContacts.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VenusTextPrimary
                        )
                    }

                    Button(
                        onClick = { refreshContacts() },
                        colors = ButtonDefaults.buttonColors(containerColor = VenusSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Refresh", fontSize = 11.sp, color = VenusLavender)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter contacts...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = VenusTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusLavender,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredContacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (hasCheckedContacts) "No contacts found matching \"$searchQuery\"" else "Loading contacts...",
                                fontSize = 12.sp,
                                color = VenusTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "V.E.N.U.S queries device ContactsContract directly via voice commands",
                                fontSize = 11.sp,
                                color = VenusTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                        items(filteredContacts) { contact ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = VenusSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = contact.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VenusTextPrimary
                                        )
                                        Text(
                                            text = contact.phoneNumber,
                                            fontSize = 11.sp,
                                            color = VenusTextSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.callContact(contact.phoneNumber) },
                                        colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call ${contact.name}",
                                            tint = VenusMidnightPurple,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", color = VenusMidnightPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceIdSettingsContent(
    viewModel: VenusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val userDisplayName = viewModel.getUserDisplayName()

    var userNameInput by remember { mutableStateOf(userDisplayName) }
    var basePitch by remember { mutableFloatStateOf(128.0f) }
    var varianceTolerance by remember { mutableFloatStateOf(0.45f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Voice Identity Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCyanGlow),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Voice ID",
                        tint = VenusCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voice Biometrics & Speaker Recognition",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VenusTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "V.E.N.U.S recognizes your acoustic footprint and addresses you by your name ($userDisplayName).",
                    fontSize = 12.sp,
                    color = VenusTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = userNameInput,
                    onValueChange = { userNameInput = it },
                    label = { Text("Primary User Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenusCyan,
                        unfocusedBorderColor = VenusCardBorder,
                        focusedTextColor = VenusTextPrimary,
                        unfocusedTextColor = VenusTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pitch Sensitivity Slider
                Text(
                    text = "Acoustic Pitch Center: ${basePitch.toInt()} Hz (Fundamental Frequency)",
                    fontSize = 12.sp,
                    color = VenusCyan,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = basePitch,
                    onValueChange = { basePitch = it },
                    valueRange = 85.0f..255.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = VenusCyan,
                        activeTrackColor = VenusCyan,
                        inactiveTrackColor = VenusSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice Match Tolerance
                Text(
                    text = "Biometric Match Tolerance: ${(varianceTolerance * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = VenusCyan,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = varianceTolerance,
                    onValueChange = { varianceTolerance = it },
                    valueRange = 0.1f..0.9f,
                    colors = SliderDefaults.colors(
                        thumbColor = VenusCyan,
                        activeTrackColor = VenusCyan,
                        inactiveTrackColor = VenusSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val chosenName = userNameInput.trim().ifEmpty { "Bharath" }
                        val newProfile = VoiceProfile(
                            userName = chosenName,
                            baselineRmsVariance = varianceTolerance,
                            baselineCadence = 2.8f,
                            baselinePitchScore = (basePitch / 128.0f).coerceIn(0.5f, 2.0f),
                            isEnrolled = true
                        )
                        viewModel.updateVoiceProfile(newProfile)
                        viewModel.speechManager.speak("Voice identity updated for $chosenName. I will address you by name.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VenusCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Save Profile",
                        tint = VenusMidnightPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Voice Profile ($userNameInput)", color = VenusMidnightPurple, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personalization Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VenusSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VenusCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PERSONALIZED CONVERSATION DIRECTIVES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = VenusLavender
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Primary User: $userDisplayName\n• Voice Responses will always address you with respect and precision as $userDisplayName\n• Wake Word \"VENUS\" activates even from lock screen\n• Start/Stop engine button engages supercar ignition acoustics",
                    fontSize = 12.sp,
                    color = VenusTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

