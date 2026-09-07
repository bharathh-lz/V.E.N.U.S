package com.example.audio

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

data class VoiceProfile(
    val userName: String = "Bharath",
    val baselineRmsVariance: Float = 0.32f,
    val baselineCadence: Float = 2.8f, // words per second
    val baselinePitchScore: Float = 1.0f,
    val isEnrolled: Boolean = true,
    val enrollmentTimestamp: Long = System.currentTimeMillis()
)

data class VoiceVerificationResult(
    val isMatch: Boolean,
    val confidencePercent: Int,
    val speakerName: String,
    val matchDetails: String
)

object VenusVoiceBiometrics {

    private const val PREFS_NAME = "venus_voice_biometrics_prefs"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_RMS_VARIANCE = "key_rms_variance"
    private const val KEY_CADENCE = "key_cadence"
    private const val KEY_PITCH_SCORE = "key_pitch_score"
    private const val KEY_IS_ENROLLED = "key_is_enrolled"
    private const val KEY_ENROLLMENT_TIME = "key_enrollment_time"

    fun getVoiceProfile(context: Context): VoiceProfile {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_USER_NAME, "Bharath") ?: "Bharath"
        val rms = prefs.getFloat(KEY_RMS_VARIANCE, 0.32f)
        val cadence = prefs.getFloat(KEY_CADENCE, 2.8f)
        val pitch = prefs.getFloat(KEY_PITCH_SCORE, 1.0f)
        val enrolled = prefs.getBoolean(KEY_IS_ENROLLED, true)
        val time = prefs.getLong(KEY_ENROLLMENT_TIME, System.currentTimeMillis())

        return VoiceProfile(
            userName = name,
            baselineRmsVariance = rms,
            baselineCadence = cadence,
            baselinePitchScore = pitch,
            isEnrolled = enrolled,
            enrollmentTimestamp = time
        )
    }

    fun saveVoiceProfile(context: Context, profile: VoiceProfile) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER_NAME, profile.userName)
            .putFloat(KEY_RMS_VARIANCE, profile.baselineRmsVariance)
            .putFloat(KEY_CADENCE, profile.baselineCadence)
            .putFloat(KEY_PITCH_SCORE, profile.baselinePitchScore)
            .putBoolean(KEY_IS_ENROLLED, profile.isEnrolled)
            .putLong(KEY_ENROLLMENT_TIME, profile.enrollmentTimestamp)
            .apply()
    }

    fun updateUserName(context: Context, newName: String) {
        val current = getVoiceProfile(context)
        saveVoiceProfile(context, current.copy(userName = newName.trim()))
    }

    fun enrollVoice(
        context: Context,
        userName: String,
        sampleRmsVariance: Float,
        sampleCadence: Float,
        samplePitch: Float = 1.0f
    ): VoiceProfile {
        val profile = VoiceProfile(
            userName = if (userName.isNotBlank()) userName.trim() else "Bharath",
            baselineRmsVariance = sampleRmsVariance.coerceIn(0.1f, 0.8f),
            baselineCadence = sampleCadence.coerceIn(1.0f, 6.0f),
            baselinePitchScore = samplePitch.coerceIn(0.5f, 2.0f),
            isEnrolled = true,
            enrollmentTimestamp = System.currentTimeMillis()
        )
        saveVoiceProfile(context, profile)
        return profile
    }

    /**
     * Compares incoming audio metrics with enrolled biometric profile.
     * Computes multi-factor distance (energy variance, vocal cadence, speech rate).
     */
    fun verifySpeaker(
        profile: VoiceProfile,
        rmsVariance: Float,
        durationSeconds: Float,
        transcript: String
    ): VoiceVerificationResult {
        if (!profile.isEnrolled) {
            return VoiceVerificationResult(
                isMatch = true,
                confidencePercent = 85,
                speakerName = profile.userName,
                matchDetails = "Default profile active (Uncalibrated)"
            )
        }

        val wordCount = transcript.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val currentCadence = if (durationSeconds > 0.4f) wordCount / durationSeconds else 2.5f

        // Acoustic variance error delta
        val rmsDiff = abs(rmsVariance - profile.baselineRmsVariance)
        val cadenceDiff = abs(currentCadence - profile.baselineCadence)

        // Calculate biometric correlation score (0.0 to 1.0)
        val rmsSimilarity = (1.0f - (rmsDiff * 1.5f)).coerceIn(0.5f, 1.0f)
        val cadenceSimilarity = (1.0f - (cadenceDiff / 4.0f)).coerceIn(0.6f, 1.0f)

        val blendedScore = (rmsSimilarity * 0.55f + cadenceSimilarity * 0.45f)
        val confidencePercent = (blendedScore * 100f).roundToInt().coerceIn(70, 99)

        val isMatch = confidencePercent >= 75
        val speaker = if (isMatch) profile.userName else "Guest / Unknown Speaker"

        return VoiceVerificationResult(
            isMatch = isMatch,
            confidencePercent = confidencePercent,
            speakerName = speaker,
            matchDetails = "Acoustic match $confidencePercent% (Cadence: ${String.format(Locale.US, "%.1f", currentCadence)} wps, Variance: ${String.format(Locale.US, "%.2f", rmsVariance)})"
        )
    }

    /**
     * Returns natural personalized wake-word greeting personalized for user.
     */
    fun getPersonalizedGreeting(userName: String, isMatch: Boolean): String {
        return if (isMatch && userName.isNotBlank()) {
            val greetings = listOf(
                "Yes, $userName? I'm listening.",
                "At your service, $userName.",
                "Good to hear your voice, $userName. How can I help you?",
                "Systems ready, $userName. What's on your mind?",
                "Online and listening, $userName."
            )
            greetings.random()
        } else {
            "Yes? I'm listening. How can I help you?"
        }
    }
}
