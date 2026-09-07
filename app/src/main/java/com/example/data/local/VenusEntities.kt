package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Bharath",
    val assistantCodename: String = "V.E.N.U.S",
    val personalityTone: String = "Sophisticated, direct, respectful, and responsive",
    val wakeWord: String = "HEY VENUS",
    val voiceOption: String = "JARVIS_BRITISH",
    val languageMode: String = "BILINGUAL_AUTO",
    val speechPitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val responseVolume: Float = 1.0f,
    val autoSpeak: Boolean = true,
    val continuousWakeListening: Boolean = true,
    val engineSoundEnabled: Boolean = true,
    val forceOfflineMode: Boolean = false,
    val customInstructions: String = "",
    val favoriteTopics: String = "AI, Supercars, Space, Technology",
    val historyContextCount: Int = 5,
    val themePreset: String = "CYBERPUNK", // "CYBERPUNK", "MINIMALIST", "CLASSIC_DARK", "TITANIUM_STEALTH", "NEON_VIPER"
    val showOrb: Boolean = true,
    val showIgnitionSwitch: Boolean = true,
    val showHudMetrics: Boolean = true,
    val showQuickCommandGrid: Boolean = true,
    val showLiveStatusTicker: Boolean = true,
    val showRecentDialoguePreview: Boolean = true,
    val orbScale: Float = 1.0f,
    val homepageGreetingStyle: String = "TACTICAL", // "TACTICAL", "CASUAL", "MINIMAL"
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userPrompt: String,
    val venusResponse: String,
    val timestamp: Long = System.currentTimeMillis(),
    val emotion: String = "NEUTRAL",
    val mode: String = "ONLINE", // "ONLINE" or "OFFLINE"
    val actionExecuted: String? = null,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "user_data_vault")
data class UserDataVaultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // e.g. "Identity", "Preferences", "Profession", "Habits", "Contacts", "Custom Rules"
    val label: String,    // e.g. "Full Name", "Favorite Language", "Primary Device"
    val encryptedValue: String, // Stored encrypted locally
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetTimeMillis: Long,
    val isCompleted: Boolean = false,
    val recurrence: String = "ONCE", // ONCE, DAILY, WEEKLY
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "smart_notes")
data class SmartNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Project Idea", // "Project Idea", "Study", "Work", "Personal", "Code", "General"
    val tags: String = "idea,ai",
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

