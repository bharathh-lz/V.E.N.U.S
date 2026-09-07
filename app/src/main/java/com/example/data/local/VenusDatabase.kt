package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserPreferencesEntity::class,
        ConversationEntity::class,
        UserDataVaultEntity::class,
        ReminderEntity::class,
        TranslationEntity::class,
        SmartNoteEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class VenusDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun conversationDao(): ConversationDao
    abstract fun userDataVaultDao(): UserDataVaultDao
    abstract fun reminderDao(): ReminderDao
    abstract fun translationDao(): TranslationDao
    abstract fun smartNoteDao(): SmartNoteDao


    companion object {
        @Volatile
        private var INSTANCE: VenusDatabase? = null

        fun getDatabase(context: Context): VenusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VenusDatabase::class.java,
                    "venus_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
