package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AssistantState {
    IDLE,
    LISTENING_WAKE_WORD,
    LISTENING_COMMAND,
    THINKING,
    SPEAKING,
    ERROR
}

enum class VenusLanguageMode(val displayName: String, val nativeName: String, val tag: String) {
    BILINGUAL_AUTO("Bilingual Auto", "English + தமிழ்", "ta_en"),
    TAMIL_ONLY("Tamil Dedicated", "தமிழ் மட்டும்", "ta"),
    ENGLISH_ONLY("English Dedicated", "English Only", "en")
}

data class VenusVoiceOption(
    val id: String,
    val name: String,
    val personaTag: String,
    val description: String,
    val pitch: Float,
    val rate: Float,
    val locale: Locale = Locale.US,
    val voiceNameFilter: String? = null
)

class VenusSpeechManager(
    private val context: Context,
    private val onCommandReceived: (String, Float, Float) -> Unit, // text, rmsVariance, durationSeconds
    private val onWakeWordDetected: (String?) -> Unit // optional command attached to wake word
) : RecognitionListener, TextToSpeech.OnInitListener {

    companion object {
        private const val PREFS_NAME = "venus_speech_settings"
        private const val KEY_VOICE_ID = "key_voice_id"
        private const val KEY_WAKE_WORD = "key_wake_word"
        private const val KEY_CONTINUOUS = "key_continuous"
        private const val KEY_PITCH = "key_pitch"
        private const val KEY_RATE = "key_rate"
        private const val KEY_AUTO_SPEAK = "key_auto_speak"
        private const val KEY_LANGUAGE_MODE = "key_language_mode"
        private const val KEY_RESPONSE_VOLUME = "key_response_volume"

        val AVAILABLE_VOICES = listOf(
            VenusVoiceOption(
                id = "venus_neural",
                name = "VENUS Core (Neural)",
                personaTag = "Futuristic AI",
                description = "Sleek, pristine digital AI with optimized clarity and cybernetic resonance.",
                pitch = 1.12f,
                rate = 1.05f,
                locale = Locale.US,
                voiceNameFilter = "female"
            ),
            VenusVoiceOption(
                id = "venus_tamil",
                name = "VENUS Tamil Neural (தமிழ்)",
                personaTag = "Tamil / தமிழ்",
                description = "Dedicated Tamil AI voice with natural Dravidian phonetics, crisp cadence, and bilingual fluency.",
                pitch = 1.05f,
                rate = 1.0f,
                locale = Locale("ta", "IN")
            ),
            VenusVoiceOption(
                id = "venus_jarvis",
                name = "VENUS Deep (Jarvis Core)",
                personaTag = "Deep Resonant AI",
                description = "Deep, calm, authoritative resonant voice for executive-level command.",
                pitch = 0.76f,
                rate = 0.95f,
                locale = Locale.US,
                voiceNameFilter = "male"
            ),
            VenusVoiceOption(
                id = "venus_cyber",
                name = "VENUS Cyberpunk Neon",
                personaTag = "Synthwave Digital",
                description = "High-octane, energetic synthesized tone with razor-sharp frequency.",
                pitch = 1.35f,
                rate = 1.15f,
                locale = Locale.US
            ),
            VenusVoiceOption(
                id = "venus_warm",
                name = "VENUS Warm Executive",
                personaTag = "Gentle & Natural",
                description = "Soothing, natural acoustic warmth designed for daily conversation and focus.",
                pitch = 1.0f,
                rate = 0.98f,
                locale = Locale.US
            ),
            VenusVoiceOption(
                id = "venus_british",
                name = "VENUS Royal British",
                personaTag = "Refined UK",
                description = "Sophisticated British cadence with measured pacing and refined tone.",
                pitch = 1.05f,
                rate = 1.0f,
                locale = Locale.UK
            ),
            VenusVoiceOption(
                id = "venus_cosmic",
                name = "VENUS Cosmic Commander",
                personaTag = "Assertive Command",
                description = "Bold, confident aerospace telemetry voice with distinct military clarity.",
                pitch = 0.88f,
                rate = 1.08f,
                locale = Locale.US
            ),
            VenusVoiceOption(
                id = "venus_whisper",
                name = "VENUS Soft Ambient",
                personaTag = "Calm Bedtime",
                description = "Low acoustic intensity and gentle tone, ideal for quiet environments.",
                pitch = 0.95f,
                rate = 0.88f,
                locale = Locale.US
            ),
            VenusVoiceOption(
                id = "venus_speed",
                name = "VENUS Velocity 2.0",
                personaTag = "Rapid Dispatch",
                description = "Ultra-fast responsive cadence for power users who want swift audio briefs.",
                pitch = 1.18f,
                rate = 1.35f,
                locale = Locale.US
            ),
            VenusVoiceOption(
                id = "venus_hindi",
                name = "VENUS Hindi AI (हिंदी)",
                personaTag = "Hindi / हिंदी",
                description = "Natural Indian Hindi voice with crisp pronunciation and balanced pitch.",
                pitch = 1.05f,
                rate = 1.0f,
                locale = Locale("hi", "IN")
            ),
            VenusVoiceOption(
                id = "venus_spanish",
                name = "VENUS Español AI",
                personaTag = "Spanish / Español",
                description = "Warm Iberian and Latin American Spanish voice with expressive inflection.",
                pitch = 1.1f,
                rate = 1.05f,
                locale = Locale("es", "ES")
            ),
            VenusVoiceOption(
                id = "venus_french",
                name = "VENUS Français AI",
                personaTag = "French / Français",
                description = "Elegant Parisian French voice with subtle tonal resonance.",
                pitch = 1.08f,
                rate = 1.0f,
                locale = Locale.FRENCH
            ),
            VenusVoiceOption(
                id = "venus_japanese",
                name = "VENUS Japanese AI (日本語)",
                personaTag = "Japanese / 日本語",
                description = "Polite Tokyo Japanese voice with futuristic anime-assistant inflection.",
                pitch = 1.25f,
                rate = 1.08f,
                locale = Locale.JAPAN
            )
        )
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    var isTtsReady = false
        private set
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _assistantState = MutableStateFlow(AssistantState.IDLE)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    private val _selectedVoice = MutableStateFlow(AVAILABLE_VOICES.first())
    val selectedVoice: StateFlow<VenusVoiceOption> = _selectedVoice.asStateFlow()

    private val _availableSystemVoices = MutableStateFlow<List<String>>(emptyList())
    val availableSystemVoices: StateFlow<List<String>> = _availableSystemVoices.asStateFlow()

    private val _languageMode = MutableStateFlow(VenusLanguageMode.BILINGUAL_AUTO)
    val languageMode: StateFlow<VenusLanguageMode> = _languageMode.asStateFlow()

    // Wake Word configured to "HEY VENUS" by default
    var wakeWord: String = "HEY VENUS"
    var isContinuousListening: Boolean = true
    var speechRate: Float = 1.05f
    var speechPitch: Float = 1.12f
    var autoSpeakResponses: Boolean = true
    
    // Reduced response sound volume (default 0.60f for a comfortable, gentle audio level)
    private val _responseVolume = MutableStateFlow(0.60f)
    val responseVolume: StateFlow<Float> = _responseVolume.asStateFlow()

    private var speechStartTime: Long = 0L
    private val rmsSamples = mutableListOf<Float>()
    private var onSpeechDoneCallback: (() -> Unit)? = null
    private var pendingInitialGreeting: Boolean = false

    init {
        loadPersistedSettings()
        initializeSpeechRecognizer()
        initializeTts()
    }

    private fun loadPersistedSettings() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val voiceId = prefs.getString(KEY_VOICE_ID, "venus_neural") ?: "venus_neural"
        val foundVoice = AVAILABLE_VOICES.find { it.id == voiceId } ?: AVAILABLE_VOICES.first()
        _selectedVoice.value = foundVoice

        wakeWord = prefs.getString(KEY_WAKE_WORD, "HEY VENUS") ?: "HEY VENUS"
        isContinuousListening = prefs.getBoolean(KEY_CONTINUOUS, true)
        speechPitch = prefs.getFloat(KEY_PITCH, foundVoice.pitch)
        speechRate = prefs.getFloat(KEY_RATE, foundVoice.rate)
        autoSpeakResponses = prefs.getBoolean(KEY_AUTO_SPEAK, true)
        _responseVolume.value = prefs.getFloat(KEY_RESPONSE_VOLUME, 0.60f)

        val modeName = prefs.getString(KEY_LANGUAGE_MODE, VenusLanguageMode.BILINGUAL_AUTO.name)
        _languageMode.value = try {
            VenusLanguageMode.valueOf(modeName ?: VenusLanguageMode.BILINGUAL_AUTO.name)
        } catch (_: Exception) {
            VenusLanguageMode.BILINGUAL_AUTO
        }
    }

    private fun savePersistedSettings() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_VOICE_ID, _selectedVoice.value.id)
            .putString(KEY_WAKE_WORD, wakeWord)
            .putBoolean(KEY_CONTINUOUS, isContinuousListening)
            .putFloat(KEY_PITCH, speechPitch)
            .putFloat(KEY_RATE, speechRate)
            .putBoolean(KEY_AUTO_SPEAK, autoSpeakResponses)
            .putString(KEY_LANGUAGE_MODE, _languageMode.value.name)
            .putFloat(KEY_RESPONSE_VOLUME, _responseVolume.value)
            .apply()
    }

    private fun initializeSpeechRecognizer() {
        mainHandler.post {
            try {
                if (SpeechRecognizer.isRecognitionAvailable(context)) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(this@VenusSpeechManager)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val currentVoiceOption = _selectedVoice.value
                val result = tts.setLanguage(currentVoiceOption.locale)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    applyVoiceOption(currentVoiceOption)

                    // Enumerate available system voices
                    try {
                        val sysVoices = tts.voices?.map { it.name } ?: emptyList()
                        _availableSystemVoices.value = sysVoices
                    } catch (_: Exception) {}

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _assistantState.value = AssistantState.SPEAKING
                        }

                        override fun onDone(utteranceId: String?) {
                            val cb = onSpeechDoneCallback
                            onSpeechDoneCallback = null

                            mainHandler.post {
                                if (cb != null) {
                                    cb.invoke()
                                } else {
                                    if (isContinuousListening) {
                                        startWakeWordListening()
                                    } else {
                                        _assistantState.value = AssistantState.IDLE
                                    }
                                }
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            val cb = onSpeechDoneCallback
                            onSpeechDoneCallback = null

                            mainHandler.post {
                                if (cb != null) {
                                    cb.invoke()
                                } else {
                                    if (isContinuousListening) {
                                        startWakeWordListening()
                                    } else {
                                        _assistantState.value = AssistantState.IDLE
                                    }
                                }
                            }
                        }
                    })

                    if (pendingInitialGreeting) {
                        pendingInitialGreeting = false
                        speakGreetingForCurrentMode()
                    }
                }
            }
        }
    }

    fun requestInitialGreeting() {
        if (isTtsReady) {
            speakGreetingForCurrentMode()
        } else {
            pendingInitialGreeting = true
        }
    }

    fun speakGreetingForCurrentMode() {
        when (_languageMode.value) {
            VenusLanguageMode.TAMIL_ONLY -> speak("வணக்கம்! நான் வீனஸ். உங்கள் பல்பணி உதவியாளர் தயார்.")
            VenusLanguageMode.BILINGUAL_AUTO -> speak("Hello! I am VENUS. வணக்கம், நான் உங்களுக்கு உதவ தயாராக உள்ளேன்.")
            VenusLanguageMode.ENGLISH_ONLY -> speak("Hello! I am VENUS. Your AI assistant is ready.")
        }
    }

    fun selectVoicePreset(voiceId: String, preview: Boolean = true) {
        val found = AVAILABLE_VOICES.find { it.id == voiceId } ?: return
        _selectedVoice.value = found
        speechPitch = found.pitch
        speechRate = found.rate
        applyVoiceOption(found)
        savePersistedSettings()

        if (preview) {
            if (found.id == "venus_tamil") {
                speak("வணக்கம்! நான் வீனஸ் தமிழ் குரல்.")
            } else {
                speak("Hello! I am VENUS")
            }
        }
    }

    fun applyVoiceOption(option: VenusVoiceOption) {
        textToSpeech?.let { tts ->
            try {
                tts.language = option.locale
                tts.setPitch(speechPitch)
                tts.setSpeechRate(speechRate)

                // Try matching system voice if available
                val voices = tts.voices
                if (!voices.isNullOrEmpty() && option.voiceNameFilter != null) {
                    val matchingVoice = voices.firstOrNull {
                        it.locale.language == option.locale.language &&
                        it.name.contains(option.voiceNameFilter, ignoreCase = true)
                    } ?: voices.firstOrNull { it.locale.language == option.locale.language }

                    if (matchingVoice != null) {
                        tts.voice = matchingVoice
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun setSystemVoiceByName(voiceName: String) {
        textToSpeech?.let { tts ->
            try {
                val targetVoice = tts.voices?.find { it.name == voiceName }
                if (targetVoice != null) {
                    tts.voice = targetVoice
                    speak("Hello! I am VENUS")
                }
            } catch (_: Exception) {}
        }
    }

    fun setLanguageMode(mode: VenusLanguageMode) {
        _languageMode.value = mode
        if (mode == VenusLanguageMode.TAMIL_ONLY) {
            val tamilVoice = AVAILABLE_VOICES.find { it.id == "venus_tamil" }
            if (tamilVoice != null) {
                _selectedVoice.value = tamilVoice
                speechPitch = tamilVoice.pitch
                speechRate = tamilVoice.rate
                applyVoiceOption(tamilVoice)
            }
        }
        savePersistedSettings()
    }

    fun setResponseVolume(volume: Float) {
        _responseVolume.value = volume.coerceIn(0.05f, 1.0f)
        savePersistedSettings()
    }

    fun startCommandListening() {
        mainHandler.post {
            stopTts()
            _assistantState.value = AssistantState.LISTENING_COMMAND
            _partialTranscript.value = ""
            rmsSamples.clear()
            speechStartTime = System.currentTimeMillis()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

                // Configure bilingual and Tamil STT detection
                when (_languageMode.value) {
                    VenusLanguageMode.TAMIL_ONLY -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ta-IN")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("ta-IN", "ta"))
                    }
                    VenusLanguageMode.BILINGUAL_AUTO -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("ta-IN", "en-US", "en-IN"))
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ta-IN")
                    }
                    VenusLanguageMode.ENGLISH_ONLY -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "en-IN"))
                    }
                }
            }

            try {
                speechRecognizer?.cancel()
                speechRecognizer?.startListening(intent)
            } catch (_: Exception) {
                _assistantState.value = AssistantState.ERROR
            }
        }
    }

    fun startWakeWordListening() {
        if (!isContinuousListening) return
        mainHandler.post {
            stopTts()
            _assistantState.value = AssistantState.LISTENING_WAKE_WORD
            _partialTranscript.value = ""

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

                // Multilingual wake word listening (English & Tamil)
                when (_languageMode.value) {
                    VenusLanguageMode.TAMIL_ONLY -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ta-IN")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("ta-IN"))
                    }
                    VenusLanguageMode.BILINGUAL_AUTO -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("ta-IN", "en-US", "en-IN"))
                    }
                    VenusLanguageMode.ENGLISH_ONLY -> {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                        putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "en-IN"))
                    }
                }
            }

            try {
                speechRecognizer?.cancel()
                speechRecognizer?.startListening(intent)
            } catch (_: Exception) {
                _assistantState.value = AssistantState.IDLE
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } catch (_: Exception) {}
            if (_assistantState.value != AssistantState.SPEAKING && _assistantState.value != AssistantState.THINKING) {
                _assistantState.value = AssistantState.IDLE
            }
        }
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        if (!isTtsReady || text.isBlank()) {
            onFinished?.invoke()
            return
        }

        // Strip action directives from speech audio
        val speechText = text.replace(Regex("""\[ACTION:[^\]]+\]"""), "").trim()
        if (speechText.isBlank()) {
            onFinished?.invoke()
            return
        }

        onSpeechDoneCallback = onFinished

        textToSpeech?.apply {
            val containsTamil = speechText.any { it in '\u0B80'..'\u0BFF' }
            val containsHindi = speechText.any { it in '\u0900'..'\u097F' }
            try {
                if (containsTamil || _languageMode.value == VenusLanguageMode.TAMIL_ONLY) {
                    language = Locale("ta", "IN")
                } else if (containsHindi) {
                    language = Locale("hi", "IN")
                } else {
                    language = _selectedVoice.value.locale
                }
            } catch (_: Exception) {}

            setPitch(speechPitch)
            setSpeechRate(speechRate)
            val utteranceId = "VENUS_UTTERANCE_${System.currentTimeMillis()}"

            // Apply reduced response volume (default 0.60f)
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, _responseVolume.value)
            }
            speak(speechText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    fun stopTts() {
        try {
            onSpeechDoneCallback = null
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    fun updateSettings(
        pitch: Float,
        rate: Float,
        wake: String,
        continuous: Boolean,
        autoSpeak: Boolean,
        mode: VenusLanguageMode = _languageMode.value,
        volume: Float = _responseVolume.value
    ) {
        speechPitch = pitch
        speechRate = rate
        wakeWord = wake.trim().uppercase(Locale.ROOT)
        isContinuousListening = continuous
        autoSpeakResponses = autoSpeak
        _languageMode.value = mode
        _responseVolume.value = volume.coerceIn(0.05f, 1.0f)

        textToSpeech?.setPitch(speechPitch)
        textToSpeech?.setSpeechRate(speechRate)
        savePersistedSettings()

        if (isContinuousListening && (_assistantState.value == AssistantState.IDLE || _assistantState.value == AssistantState.ERROR)) {
            startWakeWordListening()
        } else if (!isContinuousListening && _assistantState.value == AssistantState.LISTENING_WAKE_WORD) {
            stopListening()
        }
    }

    /**
     * Checks if text contains the wake word phrase or standard variations (HEY VENUS, VENUS, ஹே வீனஸ், etc.)
     * Returns Pair(isWakeWordPresent, commandAfterWakeWord)
     */
    fun extractWakeCommand(text: String): Pair<Boolean, String> {
        val upper = text.trim().uppercase(Locale.ROOT)
        val targetWake = wakeWord.trim().uppercase(Locale.ROOT)

        val wakePatterns = listOf(
            targetWake,
            "HEY $targetWake",
            "OK $targetWake",
            "OKAY $targetWake",
            "HI $targetWake",
            "HELLO $targetWake",
            "HEY VENUS",
            "OK VENUS",
            "OKAY VENUS",
            "HI VENUS",
            "HELLO VENUS",
            "VENUS",
            // Tamil wake words
            "ஹே வீனஸ்",
            "வணக்கம் வீனஸ்",
            "ஹலோ வீனஸ்",
            "ஏய் வீனஸ்",
            "வீனஸ்"
        ).distinct()

        for (pattern in wakePatterns) {
            val idx = if (pattern.any { it in '\u0B80'..'\u0BFF' }) {
                text.indexOf(pattern)
            } else {
                upper.indexOf(pattern)
            }

            if (idx != -1) {
                // Wake word detected! Extract command after it
                val remainder = if (pattern.any { it in '\u0B80'..'\u0BFF' }) {
                    text.substring(idx + pattern.length)
                } else {
                    upper.substring(idx + pattern.length)
                }
                    .trim()
                    .removePrefix(",")
                    .removePrefix(":")
                    .removePrefix(".")
                    .trim()
                return Pair(true, remainder)
            }
        }

        return Pair(false, "")
    }

    // Speech Recognition Callbacks
    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {
        speechStartTime = System.currentTimeMillis()
    }

    override fun onRmsChanged(rmsdB: Float) {
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
        _audioRms.value = normalized
        if (_assistantState.value == AssistantState.LISTENING_COMMAND) {
            rmsSamples.add(normalized)
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _audioRms.value = 0f
    }

    override fun onError(error: Int) {
        _audioRms.value = 0f
        mainHandler.postDelayed({
            if (isContinuousListening && _assistantState.value == AssistantState.LISTENING_WAKE_WORD) {
                startWakeWordListening()
            } else if (_assistantState.value == AssistantState.LISTENING_COMMAND) {
                if (isContinuousListening) {
                    startWakeWordListening()
                } else {
                    _assistantState.value = AssistantState.IDLE
                }
            }
        }, 300)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val bestMatch = matches?.firstOrNull() ?: ""

        val durationSeconds = ((System.currentTimeMillis() - speechStartTime) / 1000f).coerceAtLeast(0.5f)
        val variance = if (rmsSamples.isNotEmpty()) {
            val avg = rmsSamples.average().toFloat()
            val sumSq = rmsSamples.sumOf { (it - avg).toDouble() * (it - avg).toDouble() }
            (sumSq / rmsSamples.size).toFloat().coerceIn(0f, 1f)
        } else 0.3f

        if (_assistantState.value == AssistantState.LISTENING_WAKE_WORD) {
            val (isWake, command) = extractWakeCommand(bestMatch)
            if (isWake) {
                if (command.isNotBlank()) {
                    _assistantState.value = AssistantState.THINKING
                    onWakeWordDetected(command)
                    onCommandReceived(command, variance, durationSeconds)
                } else {
                    onWakeWordDetected(null)
                }
            } else {
                if (isContinuousListening) {
                    mainHandler.postDelayed({ startWakeWordListening() }, 200)
                } else {
                    _assistantState.value = AssistantState.IDLE
                }
            }
        } else if (_assistantState.value == AssistantState.LISTENING_COMMAND) {
            if (bestMatch.isNotBlank()) {
                val (isWake, command) = extractWakeCommand(bestMatch)
                val finalPrompt = if (isWake && command.isNotBlank()) command else bestMatch
                _assistantState.value = AssistantState.THINKING
                onCommandReceived(finalPrompt, variance, durationSeconds)
            } else {
                if (isContinuousListening) {
                    startWakeWordListening()
                } else {
                    _assistantState.value = AssistantState.IDLE
                }
            }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partial = partials?.firstOrNull() ?: ""
        if (partial.isNotBlank()) {
            _partialTranscript.value = partial
            if (_assistantState.value == AssistantState.LISTENING_WAKE_WORD) {
                val (isWake, command) = extractWakeCommand(partial)
                if (isWake) {
                    try {
                        speechRecognizer?.stopListening()
                    } catch (_: Exception) {}
                    if (command.isNotBlank()) {
                        _assistantState.value = AssistantState.THINKING
                        onWakeWordDetected(command)
                        onCommandReceived(command, 0.35f, 1.2f)
                    } else {
                        onWakeWordDetected(null)
                    }
                }
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
    }
}
