package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AssistantState
import com.example.audio.EmotionAnalysisResult
import com.example.audio.VenusContact
import com.example.audio.VenusContactManager
import com.example.audio.VenusEmotion
import com.example.audio.VenusEmotionDetector
import com.example.audio.VenusEngineSoundGenerator
import com.example.audio.VenusLanguageMode
import com.example.audio.VenusSpeechManager
import com.example.audio.VenusVoiceBiometrics
import com.example.audio.VenusVoiceOption
import com.example.audio.VoiceProfile
import com.example.audio.VoiceVerificationResult
import com.example.data.local.ConversationEntity
import com.example.data.local.CryptoUtils
import com.example.data.local.OfflineKnowledgeBase
import com.example.data.local.ReminderEntity
import com.example.data.local.SmartNoteEntity
import com.example.data.local.TranslationEntity
import com.example.data.local.UserDataVaultEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.local.VenusDatabase
import com.example.data.remote.GeminiService
import com.example.data.repository.VenusRepository
import com.example.service.VenusActionExecutor
import com.example.service.VenusAssistOverlayService
import com.example.service.VenusWakeLockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.util.Base64

data class UserVaultItem(
    val id: Long = 0,
    val category: String,
    val label: String,
    val decryptedValue: String
)

class VenusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VenusDatabase.getDatabase(application)
    val repository = VenusRepository(
        userPreferencesDao = db.userPreferencesDao(),
        conversationDao = db.conversationDao(),
        userDataVaultDao = db.userDataVaultDao(),
        reminderDao = db.reminderDao(),
        translationDao = db.translationDao(),
        smartNoteDao = db.smartNoteDao()
    )
    private val conversationDao = db.conversationDao()
    private val userDataVaultDao = db.userDataVaultDao()
    private val reminderDao = db.reminderDao()
    private val translationDao = db.translationDao()
    private val smartNoteDao = db.smartNoteDao()

    val actionExecutor = VenusActionExecutor(application)

    // Speech manager
    val speechManager: VenusSpeechManager = VenusSpeechManager(
        context = application,
        onCommandReceived = { command, rmsVariance, durationSeconds ->
            processUserCommand(command, rmsVariance, durationSeconds)
        },
        onWakeWordDetected = { commandAfterWake ->
            handleWakeWordDetected(commandAfterWake)
        }
    )

    // UI States
    val assistantState: StateFlow<AssistantState> = speechManager.assistantState
    val audioRms: StateFlow<Float> = speechManager.audioRms
    val partialTranscript: StateFlow<String> = speechManager.partialTranscript

    private val _currentPrompt = MutableStateFlow("")
    val currentPrompt: StateFlow<String> = _currentPrompt.asStateFlow()

    private val _currentResponse = MutableStateFlow(
        "Hello! I am VENUS"
    )
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()

    val selectedVoice: StateFlow<VenusVoiceOption> = speechManager.selectedVoice
    val availableVoices: List<VenusVoiceOption> = VenusSpeechManager.AVAILABLE_VOICES
    val availableSystemVoices: StateFlow<List<String>> = speechManager.availableSystemVoices

    private val _liveStatusMessage = MutableStateFlow("Ready — Always Listening")
    val liveStatusMessage: StateFlow<String> = _liveStatusMessage.asStateFlow()

    private val _currentEmotion = MutableStateFlow(VenusEmotion.NEUTRAL)
    val currentEmotion: StateFlow<VenusEmotion> = _currentEmotion.asStateFlow()

    private val _emotionAnalysis = MutableStateFlow(
        EmotionAnalysisResult(
            emotion = VenusEmotion.NEUTRAL,
            confidence = 0.85f,
            acousticEnergy = 0.3f,
            sentimentScore = 0f,
            suggestion = "System metrics optimal. V.E.N.U.S is listening."
        )
    )
    val emotionAnalysis: StateFlow<EmotionAnalysisResult> = _emotionAnalysis.asStateFlow()

    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    // Engine Ignition Status & Supercar Start/Stop
    private val _isEngineStarted = MutableStateFlow(true)
    val isEngineStarted: StateFlow<Boolean> = _isEngineStarted.asStateFlow()

    private val _isIgniting = MutableStateFlow(false)
    val isIgniting: StateFlow<Boolean> = _isIgniting.asStateFlow()

    private val _engineRpm = MutableStateFlow(850)
    val engineRpm: StateFlow<Int> = _engineRpm.asStateFlow()

    private var rpmJob: Job? = null

    // Voice Biometrics Profile & Verification
    private val _voiceProfile = MutableStateFlow(VenusVoiceBiometrics.getVoiceProfile(application))
    val voiceProfile: StateFlow<VoiceProfile> = _voiceProfile.asStateFlow()

    private val _latestVerification = MutableStateFlow<VoiceVerificationResult?>(null)
    val latestVerification: StateFlow<VoiceVerificationResult?> = _latestVerification.asStateFlow()

    // Vision Scanner State
    private val _visionAnalysisResult = MutableStateFlow<String?>(null)
    val visionAnalysisResult: StateFlow<String?> = _visionAnalysisResult.asStateFlow()

    private val _isAnalyzingVision = MutableStateFlow(false)
    val isAnalyzingVision: StateFlow<Boolean> = _isAnalyzingVision.asStateFlow()

    // Code Studio State
    private val _codeResult = MutableStateFlow<String?>(null)
    val codeResult: StateFlow<String?> = _codeResult.asStateFlow()

    private val _isGeneratingCode = MutableStateFlow(false)
    val isGeneratingCode: StateFlow<Boolean> = _isGeneratingCode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Kotlin")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Document Summarizer State
    private val _docSummaryResult = MutableStateFlow<String?>(null)
    val docSummaryResult: StateFlow<String?> = _docSummaryResult.asStateFlow()

    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    // Movie Recommendations State
    private val _movieRecommendations = MutableStateFlow<String?>(null)
    val movieRecommendations: StateFlow<String?> = _movieRecommendations.asStateFlow()

    private val _isLoadingMovies = MutableStateFlow(false)
    val isLoadingMovies: StateFlow<Boolean> = _isLoadingMovies.asStateFlow()

    // Ambient Synthesizer State
    private val _isPlayingSynthesizer = MutableStateFlow(false)
    val isPlayingSynthesizer: StateFlow<Boolean> = _isPlayingSynthesizer.asStateFlow()

    // Mini-Games Suite State
    private val _triviaQuestion = MutableStateFlow("What is the speed of light in vacuum?")
    val triviaQuestion: StateFlow<String> = _triviaQuestion.asStateFlow()

    private val _triviaOptions = MutableStateFlow(listOf("299,792 km/s", "150,000 km/s", "384,400 km/s", "1,080,000 km/s"))
    val triviaOptions: StateFlow<List<String>> = _triviaOptions.asStateFlow()

    private val _triviaCorrectIndex = MutableStateFlow(0)
    val triviaCorrectIndex: StateFlow<Int> = _triviaCorrectIndex.asStateFlow()

    private val _triviaSelected = MutableStateFlow<Int?>(null)
    val triviaSelected: StateFlow<Int?> = _triviaSelected.asStateFlow()

    private val _triviaScore = MutableStateFlow(0)
    val triviaScore: StateFlow<Int> = _triviaScore.asStateFlow()

    private val _tictactoeBoard = MutableStateFlow(List(9) { "" })
    val tictactoeBoard: StateFlow<List<String>> = _tictactoeBoard.asStateFlow()

    private val _tictactoeStatus = MutableStateFlow("Your turn (X)")
    val tictactoeStatus: StateFlow<String> = _tictactoeStatus.asStateFlow()

    // Settings
    private val _wakeWord = MutableStateFlow("HEY VENUS")
    val wakeWord: StateFlow<String> = _wakeWord.asStateFlow()

    val languageMode: StateFlow<VenusLanguageMode> = speechManager.languageMode
    val responseVolume: StateFlow<Float> = speechManager.responseVolume

    fun setLanguageMode(mode: VenusLanguageMode) {
        speechManager.setLanguageMode(mode)
    }

    fun setResponseVolume(volume: Float) {
        speechManager.setResponseVolume(volume)
        VenusEngineSoundGenerator.soundVolume = volume * 0.75f
    }

    private val _continuousWakeListening = MutableStateFlow(true)
    val continuousWakeListening: StateFlow<Boolean> = _continuousWakeListening.asStateFlow()

    private val _speechPitch = MutableStateFlow(1.0f)
    val speechPitch: StateFlow<Float> = _speechPitch.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _autoSpeak = MutableStateFlow(true)
    val autoSpeak: StateFlow<Boolean> = _autoSpeak.asStateFlow()

    private val _forceOfflineMode = MutableStateFlow(false)
    val forceOfflineMode: StateFlow<Boolean> = _forceOfflineMode.asStateFlow()

    private val _isOverlayActive = MutableStateFlow(false)
    val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

    // Translator States
    private val _sourceLanguage = MutableStateFlow("English")
    val sourceLanguage: StateFlow<String> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow("Spanish")
    val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

    private val _translationSourceText = MutableStateFlow("")
    val translationSourceText: StateFlow<String> = _translationSourceText.asStateFlow()

    private val _translationResultText = MutableStateFlow("")
    val translationResultText: StateFlow<String> = _translationResultText.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    // Room-persisted User Preferences
    val userPreferences: StateFlow<UserPreferencesEntity> = repository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferencesEntity())

    private val _userName = MutableStateFlow("Bharath")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _personalityTone = MutableStateFlow("Sophisticated, direct, respectful, and responsive")
    val personalityTone: StateFlow<String> = _personalityTone.asStateFlow()

    private val _customInstructions = MutableStateFlow("")
    val customInstructions: StateFlow<String> = _customInstructions.asStateFlow()

    // Homepage Customization & Theme StateFlows
    private val _themePreset = MutableStateFlow("CYBERPUNK")
    val themePreset: StateFlow<String> = _themePreset.asStateFlow()

    private val _showOrb = MutableStateFlow(true)
    val showOrb: StateFlow<Boolean> = _showOrb.asStateFlow()

    private val _showIgnitionSwitch = MutableStateFlow(true)
    val showIgnitionSwitch: StateFlow<Boolean> = _showIgnitionSwitch.asStateFlow()

    private val _showHudMetrics = MutableStateFlow(true)
    val showHudMetrics: StateFlow<Boolean> = _showHudMetrics.asStateFlow()

    private val _showQuickCommandGrid = MutableStateFlow(true)
    val showQuickCommandGrid: StateFlow<Boolean> = _showQuickCommandGrid.asStateFlow()

    private val _showLiveStatusTicker = MutableStateFlow(true)
    val showLiveStatusTicker: StateFlow<Boolean> = _showLiveStatusTicker.asStateFlow()

    private val _showRecentDialoguePreview = MutableStateFlow(true)
    val showRecentDialoguePreview: StateFlow<Boolean> = _showRecentDialoguePreview.asStateFlow()

    private val _orbScale = MutableStateFlow(1.0f)
    val orbScale: StateFlow<Float> = _orbScale.asStateFlow()

    private val _homepageGreetingStyle = MutableStateFlow("TACTICAL")
    val homepageGreetingStyle: StateFlow<String> = _homepageGreetingStyle.asStateFlow()

    // Room Flows via Repository
    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedConversations: StateFlow<List<ConversationEntity>> = repository.bookmarkedConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversationCount: StateFlow<Int> = repository.totalConversationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val reminders = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val translations = translationDao.getAllTranslations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val smartNotes = smartNoteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Study Assistant States
    private val _studyConceptResult = MutableStateFlow<String?>(null)
    val studyConceptResult: StateFlow<String?> = _studyConceptResult.asStateFlow()

    private val _isExplainingStudy = MutableStateFlow(false)
    val isExplainingStudy: StateFlow<Boolean> = _isExplainingStudy.asStateFlow()

    private val _studyQuizResult = MutableStateFlow<String?>(null)
    val studyQuizResult: StateFlow<String?> = _studyQuizResult.asStateFlow()

    private val _isGeneratingStudyQuiz = MutableStateFlow(false)
    val isGeneratingStudyQuiz: StateFlow<Boolean> = _isGeneratingStudyQuiz.asStateFlow()

    private val _studyScheduleResult = MutableStateFlow<String?>(null)
    val studyScheduleResult: StateFlow<String?> = _studyScheduleResult.asStateFlow()

    private val _isGeneratingStudySchedule = MutableStateFlow(false)
    val isGeneratingStudySchedule: StateFlow<Boolean> = _isGeneratingStudySchedule.asStateFlow()

    // Screen Awareness State
    private val _screenAnalysisResult = MutableStateFlow<String?>(null)
    val screenAnalysisResult: StateFlow<String?> = _screenAnalysisResult.asStateFlow()

    private val _isAnalyzingScreen = MutableStateFlow(false)
    val isAnalyzingScreen: StateFlow<Boolean> = _isAnalyzingScreen.asStateFlow()

    // Real-Time Web & Knowledge Search State
    private val _realTimeKnowledgeResult = MutableStateFlow<String?>(null)
    val realTimeKnowledgeResult: StateFlow<String?> = _realTimeKnowledgeResult.asStateFlow()

    private val _isSearchingKnowledge = MutableStateFlow(false)
    val isSearchingKnowledge: StateFlow<Boolean> = _isSearchingKnowledge.asStateFlow()


    private val _decryptedUserData = MutableStateFlow<List<UserVaultItem>>(emptyList())
    val decryptedUserData: StateFlow<List<UserVaultItem>> = _decryptedUserData.asStateFlow()

    init {
        loadDecryptedUserData()
        populateDefaultUserDataIfEmpty()
        observeAndApplyUserPreferences()
    }

    private fun observeAndApplyUserPreferences() {
        viewModelScope.launch {
            repository.userPreferencesFlow.collect { prefs ->
                _userName.value = prefs.userName
                _personalityTone.value = prefs.personalityTone
                _customInstructions.value = prefs.customInstructions
                _wakeWord.value = prefs.wakeWord
                _speechPitch.value = prefs.speechPitch
                _speechRate.value = prefs.speechRate
                _autoSpeak.value = prefs.autoSpeak
                _continuousWakeListening.value = prefs.continuousWakeListening
                _forceOfflineMode.value = prefs.forceOfflineMode
                _themePreset.value = prefs.themePreset
                _showOrb.value = prefs.showOrb
                _showIgnitionSwitch.value = prefs.showIgnitionSwitch
                _showHudMetrics.value = prefs.showHudMetrics
                _showQuickCommandGrid.value = prefs.showQuickCommandGrid
                _showLiveStatusTicker.value = prefs.showLiveStatusTicker
                _showRecentDialoguePreview.value = prefs.showRecentDialoguePreview
                _orbScale.value = prefs.orbScale
                _homepageGreetingStyle.value = prefs.homepageGreetingStyle

                speechManager.updateSettings(
                    pitch = prefs.speechPitch,
                    rate = prefs.speechRate,
                    wake = prefs.wakeWord,
                    continuous = prefs.continuousWakeListening,
                    autoSpeak = prefs.autoSpeak,
                    mode = try { VenusLanguageMode.valueOf(prefs.languageMode) } catch (e: Exception) { VenusLanguageMode.BILINGUAL_AUTO },
                    volume = prefs.responseVolume
                )
                VenusEngineSoundGenerator.soundVolume = prefs.responseVolume * 0.75f
            }
        }
    }

    fun loadDecryptedUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            val rawList = userDataVaultDao.getAllUserDataSync()
            val decryptedList = rawList.map { entity ->
                UserVaultItem(
                    id = entity.id,
                    category = entity.category,
                    label = entity.label,
                    decryptedValue = CryptoUtils.decrypt(entity.encryptedValue)
                )
            }
            _decryptedUserData.value = decryptedList
        }
    }

    private fun populateDefaultUserDataIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = userDataVaultDao.getAllUserDataSync()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    Triple("Identity", "User Name", "Bharath"),
                    Triple("Identity", "Assistant Codename", "V.E.N.U.S"),
                    Triple("Profession", "Primary Role", "Lead Architect & Developer"),
                    Triple("Preferences", "Favorite Focus Music", "Hypercar Engine & Synthwave"),
                    Triple("Habits", "Daily Goal", "Build intelligent, voice-controlled systems"),
                    Triple("Custom Rules", "Tone Preference", "Sophisticated, direct, respectful, and responsive")
                )
                for ((cat, label, value) in defaults) {
                    userDataVaultDao.insertData(
                        UserDataVaultEntity(
                            category = cat,
                            label = label,
                            encryptedValue = CryptoUtils.encrypt(value)
                        )
                    )
                }
                loadDecryptedUserData()
            }
        }
    }

    fun addUserDataItem(category: String, label: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataVaultDao.insertData(
                UserDataVaultEntity(
                    category = category.trim(),
                    label = label.trim(),
                    encryptedValue = CryptoUtils.encrypt(value.trim())
                )
            )
            loadDecryptedUserData()
        }
    }

    fun deleteUserDataItem(item: UserVaultItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val rawList = userDataVaultDao.getAllUserDataSync()
            rawList.find { it.id == item.id }?.let {
                userDataVaultDao.deleteData(it)
            }
            loadDecryptedUserData()
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            userDataVaultDao.clearAll()
            _decryptedUserData.value = emptyList()
        }
    }

    fun getUserDisplayName(): String {
        val prefName = _userName.value
        if (prefName.isNotBlank()) return prefName
        val storedName = _decryptedUserData.value
            .find { it.label.contains("User Name", ignoreCase = true) }
            ?.decryptedValue
            ?.trim()
        return if (!storedName.isNullOrBlank()) storedName else "Bharath"
    }

    fun toggleEngine(context: Context) {
        if (_isIgniting.value) return // Prevent toggling mid-ignition sequence
        if (_isEngineStarted.value) {
            stopEngine(context)
        } else {
            startEngine(context)
        }
    }

    fun startEngine(context: Context) {
        if (_isIgniting.value) return
        _isIgniting.value = true
        _isEngineStarted.value = false
        _liveStatusMessage.value = "Ignition Sequence Cranking..."

        // Launch RPM telemetry sweep during ignition & power sequence
        rpmJob?.cancel()
        rpmJob = viewModelScope.launch(Dispatchers.Default) {
            // Cranking phase (0 - 620ms): Electric starter spins direct-injection crank at 220 to 320 RPM
            val crankStart = System.currentTimeMillis()
            while (System.currentTimeMillis() - crankStart < 620) {
                _engineRpm.value = 220 + (0..90).random()
                kotlinx.coroutines.delay(45)
            }

            // Power Rev-up roar burst (620 - 1450ms): instant throttle flare spike to ~6200 RPM
            _liveStatusMessage.value = "Combustion Fired — Throttle Flare!"
            val revStart = System.currentTimeMillis()
            while (System.currentTimeMillis() - revStart < 830) {
                val progress = (System.currentTimeMillis() - revStart) / 830f
                _engineRpm.value = (320 + (5880 * kotlin.math.sin(progress * Math.PI))).toInt()
                kotlinx.coroutines.delay(30)
            }

            // Settle to refined sport idle (1450 - 2400ms): 6200 -> 820 RPM
            _liveStatusMessage.value = "Ignition Core Settling to Idle..."
            val settleStart = System.currentTimeMillis()
            while (System.currentTimeMillis() - settleStart < 950) {
                val p = (System.currentTimeMillis() - settleStart) / 950f
                _engineRpm.value = (6200 - (5380 * p)).toInt().coerceAtLeast(820)
                kotlinx.coroutines.delay(35)
            }

            // Steady idle flutter (~820 RPM refined hum)
            while (_isEngineStarted.value) {
                _engineRpm.value = 820 + (-12..12).random()
                kotlinx.coroutines.delay(180)
            }
        }

        VenusEngineSoundGenerator.playEngineStartSound {
            _isIgniting.value = false
            _isEngineStarted.value = true

            // Core Lifecycle Integration: Wake Lock Service & Wake Word Daemon
            VenusWakeLockService.start(context)
            startWakeWordDaemon()

            // Initialize vocal greetings
            val userName = getUserDisplayName()
            val isTamil = speechManager.languageMode.value == VenusLanguageMode.TAMIL_ONLY
            val engineGreet = if (isTamil) {
                "வணக்கம் $userName! வீனஸ் எஞ்சின் இயக்கப்பட்டது. அமைப்புகள் தயார், நான் கேட்கிறேன்."
            } else {
                "V.E.N.U.S Core ignited. Systems fully operational for Commander $userName. I am ready for your command."
            }

            _currentResponse.value = engineGreet
            _liveStatusMessage.value = "V.E.N.U.S Online — Listening for \"${_wakeWord.value}\""

            if (_autoSpeak.value) {
                viewModelScope.launch(Dispatchers.Main) {
                    speechManager.speak(engineGreet)
                }
            }
        }
    }

    fun stopEngine(context: Context) {
        _isIgniting.value = false
        _isEngineStarted.value = false
        _liveStatusMessage.value = "Ignition Core Decelerating — Standby Mode"

        // Decelerate RPM to 0
        rpmJob?.cancel()
        rpmJob = viewModelScope.launch(Dispatchers.Default) {
            val startRpm = _engineRpm.value
            for (step in 1..10) {
                _engineRpm.value = (startRpm * (1f - step / 10f)).toInt()
                kotlinx.coroutines.delay(60)
            }
            _engineRpm.value = 0
        }

        speechManager.stopListening()
        speechManager.stopTts()
        VenusWakeLockService.stop(context)
        VenusEngineSoundGenerator.playEngineStopSound()
        _currentResponse.value = "V.E.N.U.S Core in standby mode. Tap Ignition Arc to power on."
    }

    fun callContact(queryOrNumber: String) {
        val res = actionExecutor.execute("CALL_CONTACT", queryOrNumber)
        _actionFeedback.value = res.message
        _currentResponse.value = res.message
        if (_autoSpeak.value) {
            speechManager.speak(res.message)
        }
    }

    fun updateVoiceProfile(profile: VoiceProfile) {
        _voiceProfile.value = profile
        VenusVoiceBiometrics.saveVoiceProfile(getApplication(), profile)
    }

    fun startWakeWordDaemon() {
        if (_continuousWakeListening.value && _isEngineStarted.value) {
            speechManager.startWakeWordListening()
        }
    }

    fun simulateWakeWordInput(commandAfterWake: String? = null) {
        handleWakeWordDetected(commandAfterWake)
    }

    private fun handleWakeWordDetected(commandAfterWake: String?) {
        if (!_isEngineStarted.value) return

        if (!commandAfterWake.isNullOrBlank()) {
            // Wake word followed directly by a command: process and respond immediately
            processUserCommand(commandAfterWake)
        } else {
            // Wake word alone: greet user verbally and immediately open command recognition
            val userName = getUserDisplayName()
            val greeting = "Yes, $userName? I'm listening."

            _currentPrompt.value = _wakeWord.value
            _currentResponse.value = greeting
            _liveStatusMessage.value = "Wake Word Detected — Listening to $userName..."

            if (_autoSpeak.value) {
                speechManager.speak(greeting) {
                    speechManager.startCommandListening()
                }
            } else {
                speechManager.startCommandListening()
            }
        }
    }

    fun togglePushToTalk() {
        when (assistantState.value) {
            AssistantState.LISTENING_COMMAND -> {
                speechManager.stopListening()
            }
            AssistantState.SPEAKING -> {
                speechManager.stopTts()
            }
            else -> {
                speechManager.startCommandListening()
            }
        }
    }

    fun processUserCommand(prompt: String, rmsVariance: Float = 0.3f, durationSeconds: Float = 2.0f) {
        if (prompt.isBlank()) return

        _currentPrompt.value = prompt
        _liveStatusMessage.value = "Analyzing speech & sentiment..."

        // 1. Run Emotion Detection & Speaker Voice Biometrics
        val emotionResult = VenusEmotionDetector.analyze(prompt, rmsVariance, durationSeconds)
        _currentEmotion.value = emotionResult.emotion
        _emotionAnalysis.value = emotionResult

        val verification = VenusVoiceBiometrics.verifySpeaker(
            profile = _voiceProfile.value,
            rmsVariance = rmsVariance,
            durationSeconds = durationSeconds,
            transcript = prompt
        )
        _latestVerification.value = verification

        viewModelScope.launch(Dispatchers.IO) {
            val userMemoryMap = _decryptedUserData.value.associate { it.label to it.decryptedValue }
            val isOnline = isNetworkAvailable() && !_forceOfflineMode.value

            val lower = prompt.trim().lowercase(Locale.ROOT)

            // Smart Note automatic voice detection
            if (lower.startsWith("remember") || lower.contains("remember my") || lower.contains("save note") || lower.contains("take a note") || lower.contains("take note")) {
                var cleanContent = prompt
                    .replace(Regex("^(venus|hey venus|ok venus)[,\\s]*", RegexOption.IGNORE_CASE), "")
                    .replace(Regex("^(remember that|remember my|remember|take a note that|take a note:|take note:|take a note|save note:?)[,\\s]*", RegexOption.IGNORE_CASE), "")
                    .trim()
                if (cleanContent.isBlank()) cleanContent = prompt
                val category = when {
                    lower.contains("project") || lower.contains("idea") || lower.contains("app") -> "Project Idea"
                    lower.contains("study") || lower.contains("exam") || lower.contains("class") -> "Study"
                    lower.contains("code") || lower.contains("bug") || lower.contains("api") -> "Code"
                    lower.contains("meeting") || lower.contains("work") || lower.contains("client") -> "Work"
                    else -> "Personal"
                }
                val noteTitle = cleanContent.take(30).trim() + if (cleanContent.length > 30) "..." else ""
                val note = SmartNoteEntity(
                    title = noteTitle,
                    content = cleanContent,
                    category = category,
                    tags = "speech,auto-saved",
                    isPinned = false,
                    timestamp = System.currentTimeMillis()
                )
                smartNoteDao.insertNote(note)
                val responseMsg = "I have organized and saved that note under $category: \"$cleanContent\""
                _currentResponse.value = responseMsg
                _actionFeedback.value = "Smart Note Saved ($category)"
                _liveStatusMessage.value = "Note recorded in Smart Notes"
                repository.recordConversation(
                    userPrompt = prompt,
                    venusResponse = responseMsg,
                    emotion = emotionResult.emotion.name,
                    mode = "SMART_NOTE_ENGINE",
                    actionExecuted = "SAVE_SMART_NOTE: $category"
                )
                if (_autoSpeak.value) {
                    withContext(Dispatchers.Main) {
                        speechManager.speak(responseMsg)
                    }
                }
                return@launch
            }

            val finalResponse: String
            var executedActionDesc: String? = null
            var modeUsed = "OFFLINE"

            val userName = getUserDisplayName()
            // Check for Multitasking compound commands (e.g. "turn on flashlight and open camera and check battery")
            val multitaskResult = OfflineKnowledgeBase.processMultitaskInput(prompt, userMemoryMap, userName)

            if (multitaskResult.isMultitask) {
                // Execute all multitasking actions sequentially
                val executedLogs = mutableListOf<String>()
                for ((actionType, payload) in multitaskResult.actions) {
                    val actRes = actionExecutor.execute(actionType, payload)
                    executedLogs.add("$actionType: ${actRes.message}")
                }
                executedActionDesc = executedLogs.joinToString(" | ")
                _actionFeedback.value = "Multitask: ${multitaskResult.actions.size} commands executed"
                finalResponse = multitaskResult.combinedResponse
                modeUsed = "MULTITASK"
            } else {
                // Single Intent Resolution
                val offlineIntent = OfflineKnowledgeBase.processOfflineInput(prompt, userMemoryMap, userName)

                if (!isOnline) {
                    // Strictly Offline Execution
                    finalResponse = offlineIntent.response
                    modeUsed = "OFFLINE"

                    if (offlineIntent.actionType != null) {
                        val actRes = actionExecutor.execute(offlineIntent.actionType, offlineIntent.actionPayload)
                        executedActionDesc = "${offlineIntent.actionType}: ${actRes.message}"
                        _actionFeedback.value = actRes.message
                    }
                } else {
                    // Online Mode: If it's a direct hardware action, execute immediately
                    if (offlineIntent.actionType != null &&
                        (offlineIntent.actionType.startsWith("FLASHLIGHT") ||
                         offlineIntent.actionType.startsWith("VOLUME") ||
                         offlineIntent.actionType.startsWith("SETTINGS") ||
                         offlineIntent.actionType.startsWith("CALL") ||
                         offlineIntent.actionType.startsWith("SEND") ||
                         offlineIntent.actionType == "CHAT_FRIEND" ||
                         offlineIntent.actionType == "MAKE_CALL" ||
                         offlineIntent.actionType == "CHECK_BATTERY" ||
                         offlineIntent.actionType == "TELL_TIME" ||
                         offlineIntent.actionType == "TELL_DATE")
                    ) {
                        val actRes = actionExecutor.execute(offlineIntent.actionType, offlineIntent.actionPayload)
                        executedActionDesc = "${offlineIntent.actionType}: ${actRes.message}"
                        _actionFeedback.value = actRes.message
                        finalResponse = if (offlineIntent.actionType == "CHECK_BATTERY") actRes.message else offlineIntent.response
                        modeUsed = "OFFLINE_ACTION"
                    } else {
                        // Query Gemini 3.5 Flash
                        _liveStatusMessage.value = "Querying Gemini neural intelligence..."
                        val historyList = repository.getRecentConversationsForAIContext(limit = 6)
                        val resolvedUserName = getUserDisplayName()
                        val currentTone = _personalityTone.value
                        val customDirectives = _customInstructions.value

                        val geminiResult = GeminiService.generateVenusResponse(
                            prompt = prompt,
                            conversationHistory = historyList,
                            userDataVault = userMemoryMap,
                            estimatedEmotion = emotionResult.emotion.name,
                            userName = resolvedUserName,
                            personalityTone = currentTone,
                            customInstructions = customDirectives
                        )

                        if (geminiResult.isSuccess) {
                            var rawResponse = geminiResult.getOrThrow()
                            modeUsed = "ONLINE_GEMINI"

                            // Parse any action tokens [ACTION:TAG, ...], supports multiple actions for multitasking
                            val actionRegex = Regex("""\[ACTION:([A-Z_]+)(?:,\s*([^\]]+))?\]""")
                            val actionMatches = actionRegex.findAll(rawResponse).toList()
                            val onlineActionLogs = mutableListOf<String>()

                            for (match in actionMatches) {
                                val actionType = match.groupValues.getOrNull(1) ?: ""
                                val payload = match.groupValues.getOrNull(2)
                                if (actionType.isNotBlank()) {
                                    val actRes = actionExecutor.execute(actionType, payload)
                                    onlineActionLogs.add("$actionType: ${actRes.message}")
                                    _actionFeedback.value = actRes.message
                                }
                            }
                            if (onlineActionLogs.isNotEmpty()) {
                                executedActionDesc = onlineActionLogs.joinToString(" | ")
                            }

                            // Strip action directives from displayed & spoken response
                            finalResponse = rawResponse.replace(actionRegex, "").trim()
                        } else {
                            // Fallback to offline processor
                            finalResponse = offlineIntent.response
                            modeUsed = "OFFLINE_ASSISTANT"

                            if (offlineIntent.actionType != null) {
                                val actRes = actionExecutor.execute(offlineIntent.actionType, offlineIntent.actionPayload)
                                executedActionDesc = "${offlineIntent.actionType}: ${actRes.message}"
                                _actionFeedback.value = actRes.message
                            }
                        }
                    }
                }
            }

            _currentResponse.value = finalResponse
            _liveStatusMessage.value = "Response complete ($modeUsed)"

            // Save conversation to Room database via Repository
            repository.recordConversation(
                userPrompt = prompt,
                venusResponse = finalResponse,
                emotion = emotionResult.emotion.name,
                mode = modeUsed,
                actionExecuted = executedActionDesc
            )

            // Voice synthesis playback
            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak(finalResponse)
                }
            }
        }
    }

    fun translateVoiceInput(sourceText: String) {
        if (sourceText.isBlank()) return
        _translationSourceText.value = sourceText
        _isTranslating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val src = _sourceLanguage.value
            val tgt = _targetLanguage.value

            val result = if (isNetworkAvailable()) {
                GeminiService.translateText(sourceText, src, tgt)
            } else {
                // Offline fallback translation
                Result.success("[$tgt Translation]: $sourceText")
            }

            val translated = if (result.isSuccess) result.getOrThrow() else "Translation error: ${result.exceptionOrNull()?.message}"
            _translationResultText.value = translated
            _isTranslating.value = false

            // Save record
            translationDao.insertTranslation(
                TranslationEntity(
                    sourceText = sourceText,
                    translatedText = translated,
                    sourceLanguage = src,
                    targetLanguage = tgt
                )
            )

            // Speak translated audio
            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak(translated)
                }
            }
        }
    }

    fun updateLanguages(source: String, target: String) {
        _sourceLanguage.value = source
        _targetLanguage.value = target
    }

    fun addManualReminder(title: String, delayMinutes: Int = 10) {
        actionExecutor.execute("SET_REMINDER", title)
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.deleteReminder(reminder)
        }
    }

    fun toggleConversationBookmark(id: Long, isBookmarked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleConversationBookmark(id, isBookmarked)
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteConversation(id)
        }
    }

    fun clearConversationHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllConversations()
        }
    }

    fun updateUserPersona(name: String, tone: String, customInstructions: String) {
        val trimmedName = if (name.isNotBlank()) name.trim() else "Bharath"
        _userName.value = trimmedName
        _personalityTone.value = tone.trim()
        _customInstructions.value = customInstructions.trim()

        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getPreferences()
            repository.savePreferences(
                current.copy(
                    userName = trimmedName,
                    personalityTone = tone.trim(),
                    customInstructions = customInstructions.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            // Keep DataVault "User Name" and "Tone Preference" in sync
            val rawList = userDataVaultDao.getAllUserDataSync()
            rawList.find { it.label.equals("User Name", ignoreCase = true) }?.let { item ->
                userDataVaultDao.updateData(item.copy(encryptedValue = CryptoUtils.encrypt(trimmedName)))
            }
            rawList.find { it.label.equals("Tone Preference", ignoreCase = true) }?.let { item ->
                userDataVaultDao.updateData(item.copy(encryptedValue = CryptoUtils.encrypt(tone.trim())))
            }
            loadDecryptedUserData()
        }
    }

    fun updateThemePreset(presetId: String) {
        _themePreset.value = presetId
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateThemePreset(presetId)
        }
    }

    fun updateHomepageLayout(
        showOrb: Boolean,
        showIgnitionSwitch: Boolean,
        showHudMetrics: Boolean,
        showQuickCommandGrid: Boolean,
        showLiveStatusTicker: Boolean,
        showRecentDialoguePreview: Boolean,
        orbScale: Float,
        greetingStyle: String
    ) {
        _showOrb.value = showOrb
        _showIgnitionSwitch.value = showIgnitionSwitch
        _showHudMetrics.value = showHudMetrics
        _showQuickCommandGrid.value = showQuickCommandGrid
        _showLiveStatusTicker.value = showLiveStatusTicker
        _showRecentDialoguePreview.value = showRecentDialoguePreview
        _orbScale.value = orbScale
        _homepageGreetingStyle.value = greetingStyle

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateHomepageLayout(
                showOrb = showOrb,
                showIgnitionSwitch = showIgnitionSwitch,
                showHudMetrics = showHudMetrics,
                showQuickCommandGrid = showQuickCommandGrid,
                showLiveStatusTicker = showLiveStatusTicker,
                showRecentDialoguePreview = showRecentDialoguePreview,
                orbScale = orbScale,
                greetingStyle = greetingStyle
            )
        }
    }

    fun clearTranslationHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            translationDao.clearAll()
        }
    }

    fun updateSettings(
        wakeWord: String,
        continuous: Boolean,
        pitch: Float,
        rate: Float,
        autoSpeak: Boolean,
        forceOffline: Boolean,
        mode: VenusLanguageMode = speechManager.languageMode.value,
        volume: Float = speechManager.responseVolume.value
    ) {
        _wakeWord.value = wakeWord
        _continuousWakeListening.value = continuous
        _speechPitch.value = pitch
        _speechRate.value = rate
        _autoSpeak.value = autoSpeak
        _forceOfflineMode.value = forceOffline

        VenusEngineSoundGenerator.soundVolume = volume * 0.75f

        speechManager.updateSettings(
            pitch = pitch,
            rate = rate,
            wake = wakeWord,
            continuous = continuous,
            autoSpeak = autoSpeak,
            mode = mode,
            volume = volume
        )

        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getPreferences()
            repository.savePreferences(
                current.copy(
                    wakeWord = wakeWord.trim().uppercase(),
                    continuousWakeListening = continuous,
                    speechPitch = pitch,
                    speechRate = rate,
                    autoSpeak = autoSpeak,
                    forceOfflineMode = forceOffline,
                    languageMode = mode.name,
                    responseVolume = volume,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleOverlayService(context: Context, enable: Boolean) {
        val intent = Intent(context, VenusAssistOverlayService::class.java)
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                // Request overlay permission
                val permIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(permIntent)
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _isOverlayActive.value = true
        } else {
            intent.action = VenusAssistOverlayService.ACTION_STOP
            context.startService(intent)
            _isOverlayActive.value = false
        }
    }

    fun analyzeImage(bitmap: Bitmap, customPrompt: String = "") {
        _isAnalyzingVision.value = true
        _visionAnalysisResult.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                val prompt = if (customPrompt.isNotBlank()) customPrompt else "Analyze this image in detail. Identify any faces, people, emotions, objects, text/OCR, landmarks, and scene context for Bharath."
                
                val result = GeminiService.analyzeImage(
                    base64Image = base64,
                    mimeType = "image/jpeg",
                    promptText = prompt,
                    userName = "Bharath"
                )
                
                val analysisText = if (result.isSuccess) {
                    result.getOrThrow()
                } else {
                    "Vision analysis unavailable: ${result.exceptionOrNull()?.message ?: "Check internet connection"}"
                }
                
                _visionAnalysisResult.value = analysisText
                _isAnalyzingVision.value = false
                
                if (_autoSpeak.value) {
                    withContext(Dispatchers.Main) {
                        speechManager.speak(analysisText.take(250))
                    }
                }
            } catch (e: Exception) {
                _visionAnalysisResult.value = "Vision error: ${e.message}"
                _isAnalyzingVision.value = false
            }
        }
    }

    fun generateOrDebugCode(task: String, language: String = "Kotlin") {
        if (task.isBlank()) return
        _isGeneratingCode.value = true
        _selectedLanguage.value = language
        _codeResult.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val result = GeminiService.generateOrDebugCode(task, language, "Bharath")
            _codeResult.value = if (result.isSuccess) {
                result.getOrThrow()
            } else {
                "// Offline Code Generator Template\nfun main() {\n    println(\"V.E.N.U.S generated: $task ($language)\")\n}"
            }
            _isGeneratingCode.value = false
        }
    }

    fun summarizeDocument(text: String) {
        if (text.isBlank()) return
        _isSummarizing.value = true
        _docSummaryResult.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val result = GeminiService.summarizeDocument(text, "Bharath")
            val summary = if (result.isSuccess) {
                result.getOrThrow()
            } else {
                "• Executive Summary: Document analyzed locally.\n• Key Point 1: Length is ${text.length} characters.\n• Key Point 2: ${text.take(120)}...\n• Key Point 3: Connect to Gemini neural cloud for deep semantic breakdown."
            }
            _docSummaryResult.value = summary
            _isSummarizing.value = false
            
            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak("Here is your document summary, Bharath: " + summary.take(200))
                }
            }
        }
    }

    fun fetchMovieRecommendations(genreOrMood: String = "Sci-Fi & Cyberpunk") {
        _isLoadingMovies.value = true
        _movieRecommendations.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val result = GeminiService.recommendMovies(genreOrMood, "Bharath")
            val recs = if (result.isSuccess) {
                result.getOrThrow()
            } else {
                "1. Blade Runner 2049 (2017, Sci-Fi) - 8.0/10\n2. Interstellar (2014, Sci-Fi) - 8.7/10\n3. The Matrix (1999, Cyberpunk) - 8.7/10\n4. Inception (2010, Thriller) - 8.8/10"
            }
            _movieRecommendations.value = recs
            _isLoadingMovies.value = false
        }
    }

    fun toggleAmbientSynthesizer() {
        _isPlayingSynthesizer.value = !_isPlayingSynthesizer.value
        if (_isPlayingSynthesizer.value) {
            VenusEngineSoundGenerator.playIgnitionSound()
        }
    }

    fun answerTrivia(index: Int) {
        if (_triviaSelected.value != null) return
        _triviaSelected.value = index
        if (index == _triviaCorrectIndex.value) {
            _triviaScore.value += 100
            VenusEngineSoundGenerator.playBeepSound()
        }
    }

    fun nextTrivia() {
        val questions = listOf(
            Triple("What is the speed of light in vacuum?", listOf("299,792 km/s", "150,000 km/s", "384,400 km/s", "1,080,000 km/s"), 0),
            Triple("Which celestial body has the strongest magnetic field?", listOf("Jupiter", "Magnetar Neutron Star", "The Sun", "Earth"), 1),
            Triple("Who is widely regarded as the first computer programmer?", listOf("Alan Turing", "Ada Lovelace", "Grace Hopper", "Charles Babbage"), 1),
            Triple("What is the primary gas in Venus's dense atmosphere?", listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Methane"), 2),
            Triple("Which quantum phenomenon allows particles to be instantly connected across distances?", listOf("Entanglement", "Superposition", "Tunneling", "Decoherence"), 0)
        )
        val nextQ = questions.random()
        _triviaQuestion.value = nextQ.first
        _triviaOptions.value = nextQ.second
        _triviaCorrectIndex.value = nextQ.third
        _triviaSelected.value = null
    }

    fun playTicTacToeMove(index: Int) {
        val board = _tictactoeBoard.value.toMutableList()
        if (board[index].isNotEmpty() || _tictactoeStatus.value.contains("wins") || _tictactoeStatus.value.contains("Draw")) return
        board[index] = "X"
        _tictactoeBoard.value = board

        // Check X win
        if (checkWin(board, "X")) {
            _tictactoeStatus.value = "Bharath wins! 🎉"
            return
        }
        if (board.none { it.isEmpty() }) {
            _tictactoeStatus.value = "It's a draw!"
            return
        }

        // VENUS AI move
        val emptySpots = board.indices.filter { board[it].isEmpty() }
        if (emptySpots.isNotEmpty()) {
            val aiMove = emptySpots.random()
            board[aiMove] = "O"
            _tictactoeBoard.value = board
            if (checkWin(board, "O")) {
                _tictactoeStatus.value = "V.E.N.U.S AI wins! 🤖"
            } else if (board.none { it.isEmpty() }) {
                _tictactoeStatus.value = "It's a draw!"
            } else {
                _tictactoeStatus.value = "Your turn (X)"
            }
        }
    }

    private fun checkWin(b: List<String>, player: String): Boolean {
        val wins = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        return wins.any { win -> win.all { b[it] == player } }
    }

    fun resetTicTacToe() {
        _tictactoeBoard.value = List(9) { "" }
        _tictactoeStatus.value = "Your turn (X)"
    }

    fun testPersonalizedDataBriefing() {
        val memory = _decryptedUserData.value
        val name = memory.find { it.label.contains("Name", ignoreCase = true) }?.decryptedValue ?: "User"
        val role = memory.find { it.label.contains("Role", ignoreCase = true) || it.label.contains("Profession", ignoreCase = true) }?.decryptedValue ?: "Specialist"
        val testPrompt = "Generate my personalized status briefing for $name ($role) using my stored profile."
        processUserCommand(testPrompt)
    }

    fun speakVenusGreeting() {
        val greeting = "Hello! I am VENUS"
        _currentResponse.value = greeting
        _liveStatusMessage.value = "Greeting Active"
        speechManager.speak(greeting)
    }

    fun onAppOpened() {
        _currentResponse.value = "Hello! I am VENUS"
        _liveStatusMessage.value = "Online — Listening"
        speechManager.requestInitialGreeting()
    }

    fun selectVoicePreset(voiceId: String, preview: Boolean = true) {
        val voice = VenusSpeechManager.AVAILABLE_VOICES.find { it.id == voiceId } ?: return
        _speechPitch.value = voice.pitch
        _speechRate.value = voice.rate
        speechManager.selectVoicePreset(voiceId, preview = false)
        if (preview) {
            speakVenusGreeting()
        }
    }

    // ==========================================
    // 4. SMART NOTES ENGINE
    // ==========================================
    fun saveSmartNote(
        title: String,
        content: String,
        category: String = "Project Idea",
        tags: String = "idea,ai",
        isPinned: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = SmartNoteEntity(
                title = title.ifBlank { "Untitled Note" },
                content = content,
                category = category,
                tags = tags,
                isPinned = isPinned,
                timestamp = System.currentTimeMillis()
            )
            smartNoteDao.insertNote(note)
            val ack = "Saved note \"$title\" to your $category vault."
            _actionFeedback.value = ack
            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak(ack)
                }
            }
        }
    }

    fun deleteSmartNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            smartNoteDao.deleteNoteById(id)
            _actionFeedback.value = "Note deleted."
        }
    }

    fun togglePinSmartNote(note: SmartNoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            smartNoteDao.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun speakSmartNote(note: SmartNoteEntity) {
        val speechText = "${note.title}. Category: ${note.category}. ${note.content}"
        speechManager.speak(speechText)
    }

    // ==========================================
    // 9. STUDY ASSISTANT MODE
    // ==========================================
    fun explainStudyConcept(concept: String, style: String = "Feynman Technique (Simple Analogies)") {
        if (concept.isBlank()) return
        _isExplainingStudy.value = true
        _studyConceptResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val userName = getUserDisplayName()
            val result = if (isNetworkAvailable()) {
                GeminiService.explainStudyConcept(concept, style, userName)
            } else {
                OfflineKnowledgeBase.explainConceptOffline(concept)
            }

            val text = if (result.isSuccess) result.getOrThrow() else "Could not explain concept: ${result.exceptionOrNull()?.message}"
            _studyConceptResult.value = text
            _isExplainingStudy.value = false

            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak("Here is the explanation for $concept. $text")
                }
            }
        }
    }

    fun generateStudyQuiz(topic: String, count: Int = 3, difficulty: String = "Intermediate") {
        if (topic.isBlank()) return
        _isGeneratingStudyQuiz.value = true
        _studyQuizResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val userName = getUserDisplayName()
            val result = if (isNetworkAvailable()) {
                GeminiService.generateStudyQuiz(topic, count, difficulty, userName)
            } else {
                Result.success("Offline Quiz on $topic:\n1. What is the fundamental principle of $topic?\nA) Standard model\nB) Dynamic recursion\nC) Conservation\nD) None\nCorrect: A")
            }

            val text = if (result.isSuccess) result.getOrThrow() else "Could not generate quiz."
            _studyQuizResult.value = text
            _isGeneratingStudyQuiz.value = false

            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak("Your $difficulty quiz on $topic is ready.")
                }
            }
        }
    }

    fun generateStudySchedule(subjects: String, dailyHours: String = "3 hours", goal: String = "Exam Preparation") {
        if (subjects.isBlank()) return
        _isGeneratingStudySchedule.value = true
        _studyScheduleResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val userName = getUserDisplayName()
            val result = if (isNetworkAvailable()) {
                GeminiService.generateStudySchedule(subjects, dailyHours, goal, userName)
            } else {
                Result.success("Daily Study Timetable for $userName:\n- Block 1 (45m): $subjects Core Focus\n- Break (10m)\n- Block 2 (45m): Active Practice\n- Review (20m): Flashcards")
            }

            val text = if (result.isSuccess) result.getOrThrow() else "Could not generate schedule."
            _studyScheduleResult.value = text
            _isGeneratingStudySchedule.value = false

            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak("I've compiled your customized study timetable.")
                }
            }
        }
    }

    // ==========================================
    // 3. SCREEN AWARENESS ENGINE
    // ==========================================
    fun analyzeScreenContent(screenSummaryOrOcr: String, userGoal: String = "What is on my screen and what actions can I take?") {
        if (screenSummaryOrOcr.isBlank()) return
        _isAnalyzingScreen.value = true
        _screenAnalysisResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val userName = getUserDisplayName()
            val result = if (isNetworkAvailable()) {
                GeminiService.analyzeScreenContent(screenSummaryOrOcr, userGoal, userName)
            } else {
                Result.success("Screen Context Analysis:\nActive view contains text:\n$screenSummaryOrOcr\n\nSuggested actions: Copy text, extract key items, set reminder.")
            }

            val text = if (result.isSuccess) result.getOrThrow() else "Screen analysis failed."
            _screenAnalysisResult.value = text
            _isAnalyzingScreen.value = false

            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak("Screen analysis complete. $text")
                }
            }
        }
    }

    // ==========================================
    // 7. REAL-TIME INFORMATION & WEB KNOWLEDGE
    // ==========================================
    fun searchRealTimeKnowledge(query: String) {
        if (query.isBlank()) return
        _isSearchingKnowledge.value = true
        _realTimeKnowledgeResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val userName = getUserDisplayName()
            val result = if (isNetworkAvailable()) {
                GeminiService.searchRealTimeKnowledge(query, userName)
            } else {
                OfflineKnowledgeBase.queryOfflineKnowledge(query)
            }

            val text = if (result.isSuccess) result.getOrThrow() else "Information unavailable."
            _realTimeKnowledgeResult.value = text
            _isSearchingKnowledge.value = false

            if (_autoSpeak.value) {
                withContext(Dispatchers.Main) {
                    speechManager.speak(text)
                }
            }
        }
    }


    fun setSystemVoice(voiceName: String) {
        speechManager.setSystemVoiceByName(voiceName)
        _currentResponse.value = "Hello! I am VENUS"
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
