package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getPreferencesFlow(): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreferencesSync(): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: UserPreferencesEntity)

    @Query("UPDATE user_preferences SET userName = :name, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateUserName(name: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET personalityTone = :tone, customInstructions = :customInstructions, updatedAt = :timestamp WHERE id = 1")
    suspend fun updatePersonality(tone: String, customInstructions: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET wakeWord = :wakeWord, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateWakeWord(wakeWord: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET voiceOption = :voice, languageMode = :mode, speechPitch = :pitch, speechRate = :rate, responseVolume = :volume, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVoiceSettings(voice: String, mode: String, pitch: Float, rate: Float, volume: Float, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET autoSpeak = :autoSpeak, continuousWakeListening = :continuous, forceOfflineMode = :forceOffline, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateSwitches(autoSpeak: Boolean, continuous: Boolean, forceOffline: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET themePreset = :themePreset, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateThemePreset(themePreset: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_preferences SET showOrb = :showOrb, showIgnitionSwitch = :showIgnitionSwitch, showHudMetrics = :showHudMetrics, showQuickCommandGrid = :showQuickCommandGrid, showLiveStatusTicker = :showLiveStatusTicker, showRecentDialoguePreview = :showRecentDialoguePreview, orbScale = :orbScale, homepageGreetingStyle = :greetingStyle, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateHomepageLayout(
        showOrb: Boolean,
        showIgnitionSwitch: Boolean,
        showHudMetrics: Boolean,
        showQuickCommandGrid: Boolean,
        showLiveStatusTicker: Boolean,
        showRecentDialoguePreview: Boolean,
        orbScale: Float,
        greetingStyle: String,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM user_preferences")
    suspend fun clearPreferences()
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY timestamp DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentConversations(limit: Int): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentConversationsSync(limit: Int): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE userPrompt LIKE '%' || :query || '%' OR venusResponse LIKE '%' || :query || '%' OR emotion LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Query("UPDATE conversations SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("SELECT COUNT(*) FROM conversations")
    fun getConversationCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun getConversationCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("DELETE FROM conversations")
    suspend fun clearAll()

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)
}

@Dao
interface UserDataVaultDao {
    @Query("SELECT * FROM user_data_vault ORDER BY category ASC, timestamp DESC")
    fun getAllUserData(): Flow<List<UserDataVaultEntity>>

    @Query("SELECT * FROM user_data_vault")
    suspend fun getAllUserDataSync(): List<UserDataVaultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(item: UserDataVaultEntity): Long

    @Update
    suspend fun updateData(item: UserDataVaultEntity)

    @Delete
    suspend fun deleteData(item: UserDataVaultEntity)

    @Query("DELETE FROM user_data_vault")
    suspend fun clearAll()
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, targetTimeMillis ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND targetTimeMillis <= :currentTime")
    suspend fun getDueReminders(currentTime: Long): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)
}

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAllTranslations(): Flow<List<TranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity): Long

    @Query("DELETE FROM translations")
    suspend fun clearAll()
}

@Dao
interface SmartNoteDao {
    @Query("SELECT * FROM smart_notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<SmartNoteEntity>>

    @Query("SELECT * FROM smart_notes WHERE category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getNotesByCategory(category: String): Flow<List<SmartNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SmartNoteEntity): Long

    @Update
    suspend fun updateNote(note: SmartNoteEntity)

    @Delete
    suspend fun deleteNote(note: SmartNoteEntity)

    @Query("DELETE FROM smart_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM smart_notes")
    suspend fun clearAll()
}

