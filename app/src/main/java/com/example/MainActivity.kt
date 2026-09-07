package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.ui.VenusViewModel
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SettingsSection
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VenusBgDark

class MainActivity : ComponentActivity() {

    private val viewModel: VenusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreset by viewModel.themePreset.collectAsState()
            MyApplicationTheme(themePreset = themePreset) {
                VenusAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("SUMMON_VENUS", false)) {
            viewModel.speechManager.startCommandListening()
        }
    }
}

@Composable
fun VenusAppContent(viewModel: VenusViewModel) {
    val context = LocalContext.current
    var isSettingsOpen by remember { mutableStateOf(false) }
    var selectedSettingsSection by remember { mutableStateOf(SettingsSection.VOICE_AUDIO) }

    // Request necessary runtime permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.RECORD_AUDIO] == true) {
            viewModel.startWakeWordDaemon()
        }
    }

    LaunchedEffect(Unit) {
        // Greet user with "Hello! I am VENUS" whenever the app opens
        viewModel.onAppOpened()

        val neededPerms = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            neededPerms.add(Manifest.permission.RECORD_AUDIO)
        } else {
            viewModel.startWakeWordDaemon()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                neededPerms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (neededPerms.isNotEmpty()) {
            permissionLauncher.launch(neededPerms.toTypedArray())
        }
    }

    // ViewModel State Collection
    val assistantState by viewModel.assistantState.collectAsState()
    val audioRms by viewModel.audioRms.collectAsState()
    val partialTranscript by viewModel.partialTranscript.collectAsState()
    val currentPrompt by viewModel.currentPrompt.collectAsState()
    val currentResponse by viewModel.currentResponse.collectAsState()
    val currentEmotion by viewModel.currentEmotion.collectAsState()
    val emotionAnalysis by viewModel.emotionAnalysis.collectAsState()
    val actionFeedback by viewModel.actionFeedback.collectAsState()

    val wakeWord by viewModel.wakeWord.collectAsState()
    val isContinuousWake by viewModel.continuousWakeListening.collectAsState()
    val speechPitch by viewModel.speechPitch.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val autoSpeak by viewModel.autoSpeak.collectAsState()
    val forceOffline by viewModel.forceOfflineMode.collectAsState()
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()

    val userDataItems by viewModel.decryptedUserData.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val translations by viewModel.translations.collectAsState()

    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val sourceText by viewModel.translationSourceText.collectAsState()
    val translatedResult by viewModel.translationResultText.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VenusBgDark)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = isSettingsOpen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { inSettings ->
                if (inSettings) {
                    SettingsScreen(
                        viewModel = viewModel,
                        wakeWord = wakeWord,
                        isContinuousWake = isContinuousWake,
                        speechPitch = speechPitch,
                        speechRate = speechRate,
                        autoSpeak = autoSpeak,
                        forceOffline = forceOffline,
                        isOverlayActive = isOverlayActive,
                        userDataItems = userDataItems,
                        conversations = conversations,
                        reminders = reminders,
                        translations = translations,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        sourceText = sourceText,
                        translatedResult = translatedResult,
                        isTranslating = isTranslating,
                        onNavigateBack = { isSettingsOpen = false },
                        initialSection = selectedSettingsSection
                    )
                } else {
                    AssistantScreen(
                        viewModel = viewModel,
                        assistantState = assistantState,
                        audioRms = audioRms,
                        partialTranscript = partialTranscript,
                        currentPrompt = currentPrompt,
                        currentResponse = currentResponse,
                        currentEmotion = currentEmotion,
                        emotionAnalysis = emotionAnalysis,
                        actionFeedback = actionFeedback,
                        wakeWord = wakeWord,
                        isContinuousWake = isContinuousWake,
                        onOpenSettings = {
                            selectedSettingsSection = SettingsSection.VOICE_AUDIO
                            isSettingsOpen = true
                        }
                    )
                }
            }
        }
    }
}
