package com.example.data.repository

import com.example.data.local.ConversationDao
import com.example.data.local.ConversationEntity
import com.example.data.local.ReminderDao
import com.example.data.local.ReminderEntity
import com.example.data.local.SmartNoteDao
import com.example.data.local.SmartNoteEntity
import com.example.data.local.TranslationDao
import com.example.data.local.TranslationEntity
import com.example.data.local.UserDataVaultDao
import com.example.data.local.UserDataVaultEntity
import com.example.data.local.UserPreferencesDao
import com.example.data.local.UserPreferencesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Single source of truth repository for V.E.N.U.S AI.
 * Encapsulates all Room database interactions for:
 * - Persistent User Preferences (name, persona tone, custom directives, voice settings, wake word)
 * - Persistent Chat & Dialogue History (prompts, responses, emotional state, actions, bookmarks)
 * - User Data Vault (encrypted identity items)
 * - Tasks, Translations, and Smart Notes
 */
class VenusRepository(
    private val userPreferencesDao: UserPreferencesDao,
    private val conversationDao: ConversationDao,
    private val userDataVaultDao: UserDataVaultDao,
    private val reminderDao: ReminderDao,
    private val translationDao: TranslationDao,
    private val smartNoteDao: SmartNoteDao
) {
    // -------------------------------------------------------------
    // USER PREFERENCES PERSISTENCE
    // -------------------------------------------------------------

    /**
     * Continuous Flow of user preferences. Emits a default entity if table is initially empty.
     */
    val userPreferencesFlow: Flow<UserPreferencesEntity> = userPreferencesDao.getPreferencesFlow()
        .map { it ?: UserPreferencesEntity() }

    suspend fun getPreferences(): UserPreferencesEntity = withContext(Dispatchers.IO) {
        userPreferencesDao.getPreferencesSync() ?: run {
            val defaultPrefs = UserPreferencesEntity()
            userPreferencesDao.insertOrUpdate(defaultPrefs)
            defaultPrefs
        }
    }

    suspend fun savePreferences(preferences: UserPreferencesEntity) = withContext(Dispatchers.IO) {
        userPreferencesDao.insertOrUpdate(preferences.copy(id = 1, updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateUserName(name: String) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(current.copy(userName = name.trim(), updatedAt = System.currentTimeMillis()))
    }

    suspend fun updatePersonality(tone: String, customInstructions: String) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(
                personalityTone = tone.trim(),
                customInstructions = customInstructions.trim(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateWakeWord(wakeWord: String) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(wakeWord = wakeWord.trim().uppercase(), updatedAt = System.currentTimeMillis())
        )
    }

    suspend fun updateVoiceSettings(
        voiceOption: String,
        languageMode: String,
        pitch: Float,
        rate: Float,
        volume: Float
    ) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(
                voiceOption = voiceOption,
                languageMode = languageMode,
                speechPitch = pitch,
                speechRate = rate,
                responseVolume = volume,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateSystemSwitches(
        autoSpeak: Boolean,
        continuousWake: Boolean,
        engineSound: Boolean,
        forceOffline: Boolean
    ) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(
                autoSpeak = autoSpeak,
                continuousWakeListening = continuousWake,
                engineSoundEnabled = engineSound,
                forceOfflineMode = forceOffline,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateThemePreset(themePreset: String) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(
                themePreset = themePreset,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateHomepageLayout(
        showOrb: Boolean,
        showIgnitionSwitch: Boolean,
        showHudMetrics: Boolean,
        showQuickCommandGrid: Boolean,
        showLiveStatusTicker: Boolean,
        showRecentDialoguePreview: Boolean,
        orbScale: Float,
        greetingStyle: String
    ) = withContext(Dispatchers.IO) {
        val current = getPreferences()
        userPreferencesDao.insertOrUpdate(
            current.copy(
                showOrb = showOrb,
                showIgnitionSwitch = showIgnitionSwitch,
                showHudMetrics = showHudMetrics,
                showQuickCommandGrid = showQuickCommandGrid,
                showLiveStatusTicker = showLiveStatusTicker,
                showRecentDialoguePreview = showRecentDialoguePreview,
                orbScale = orbScale,
                homepageGreetingStyle = greetingStyle,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // -------------------------------------------------------------
    // CHAT & DIALOGUE HISTORY PERSISTENCE
    // -------------------------------------------------------------

    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    val bookmarkedConversations: Flow<List<ConversationEntity>> = conversationDao.getBookmarkedConversations()

    val totalConversationCount: Flow<Int> = conversationDao.getConversationCount()

    fun searchConversations(query: String): Flow<List<ConversationEntity>> {
        return conversationDao.searchConversations(query)
    }

    suspend fun recordConversation(
        userPrompt: String,
        venusResponse: String,
        emotion: String,
        mode: String,
        actionExecuted: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val entity = ConversationEntity(
            userPrompt = userPrompt.trim(),
            venusResponse = venusResponse.trim(),
            emotion = emotion,
            mode = mode,
            actionExecuted = actionExecuted,
            timestamp = System.currentTimeMillis()
        )
        conversationDao.insertConversation(entity)
    }

    /**
     * Returns the recent conversations formatted chronologically (oldest to newest)
     * so that Gemini Neural Intelligence has correct conversational flow and context.
     */
    suspend fun getRecentConversationsForAIContext(limit: Int = 5): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        val rawRecent = conversationDao.getRecentConversationsSync(limit)
        // Reverse so that oldest is first and newest is last in conversation context
        rawRecent.asReversed().map { Pair(it.userPrompt, it.venusResponse) }
    }

    suspend fun toggleConversationBookmark(id: Long, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.updateBookmark(id, isBookmarked)
    }

    suspend fun deleteConversation(id: Long) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversationById(id)
    }

    suspend fun clearAllConversations() = withContext(Dispatchers.IO) {
        conversationDao.clearAll()
    }

    // -------------------------------------------------------------
    // VAULT, REMINDERS, TRANSLATIONS, NOTES
    // -------------------------------------------------------------

    val userDataVaultFlow: Flow<List<UserDataVaultEntity>> = userDataVaultDao.getAllUserData()

    suspend fun getAllUserDataSync(): List<UserDataVaultEntity> = withContext(Dispatchers.IO) {
        userDataVaultDao.getAllUserDataSync()
    }

    suspend fun insertUserData(category: String, label: String, encryptedValue: String) = withContext(Dispatchers.IO) {
        userDataVaultDao.insertData(
            UserDataVaultEntity(
                category = category,
                label = label,
                encryptedValue = encryptedValue,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteUserData(item: UserDataVaultEntity) = withContext(Dispatchers.IO) {
        userDataVaultDao.deleteData(item)
    }

    suspend fun clearUserData() = withContext(Dispatchers.IO) {
        userDataVaultDao.clearAll()
    }

    val remindersFlow: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()

    suspend fun insertReminder(title: String, targetTimeMillis: Long, recurrence: String = "ONCE") = withContext(Dispatchers.IO) {
        reminderDao.insertReminder(
            ReminderEntity(
                title = title,
                targetTimeMillis = targetTimeMillis,
                recurrence = recurrence,
                isCompleted = false
            )
        )
    }

    suspend fun deleteReminder(id: Long) = withContext(Dispatchers.IO) {
        reminderDao.deleteReminderById(id)
    }

    val translationsFlow: Flow<List<TranslationEntity>> = translationDao.getAllTranslations()

    suspend fun insertTranslation(source: String, translated: String, srcLang: String, tgtLang: String) = withContext(Dispatchers.IO) {
        translationDao.insertTranslation(
            TranslationEntity(
                sourceText = source,
                translatedText = translated,
                sourceLanguage = srcLang,
                targetLanguage = tgtLang
            )
        )
    }

    suspend fun clearTranslations() = withContext(Dispatchers.IO) {
        translationDao.clearAll()
    }

    val smartNotesFlow: Flow<List<SmartNoteEntity>> = smartNoteDao.getAllNotes()

    suspend fun insertSmartNote(title: String, content: String, category: String, tags: String = "ai") = withContext(Dispatchers.IO) {
        smartNoteDao.insertNote(
            SmartNoteEntity(
                title = title,
                content = content,
                category = category,
                tags = tags
            )
        )
    }

    suspend fun deleteSmartNote(id: Long) = withContext(Dispatchers.IO) {
        smartNoteDao.deleteNoteById(id)
    }
}
