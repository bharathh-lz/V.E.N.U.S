package com.example.audio

import java.util.Locale

enum class VenusEmotion(
    val displayName: String,
    val description: String,
    val responseTone: String,
    val iconName: String
) {
    CALM(
        displayName = "Calm & Focused",
        description = "Balanced acoustic cadence with steady pitch and tone",
        responseTone = "Serene, clear, and composed",
        iconName = "Spa"
    ),
    HAPPY(
        displayName = "Happy & Upbeat",
        description = "Positive acoustic inflection and vibrant lexical polarity",
        responseTone = "Warm, enthusiastic, and cheerful",
        iconName = "SentimentSatisfied"
    ),
    EXCITED(
        displayName = "Excited & Energetic",
        description = "High RMS acoustic energy and rapid syllable rate",
        responseTone = "Dynamic, motivating, and sharp",
        iconName = "Bolt"
    ),
    STRESSED(
        displayName = "Stressed / Urgent",
        description = "Elevated pitch variance with urgent cadence",
        responseTone = "Gentle, supportive, and reassuring",
        iconName = "Psychology"
    ),
    SAD(
        displayName = "Fatigued / Melancholic",
        description = "Lower acoustic energy with muted vocal inflection",
        responseTone = "Compassionate, uplifting, and caring",
        iconName = "Favorite"
    ),
    NEUTRAL(
        displayName = "Neutral State",
        description = "Standard operational baseline speech metrics",
        responseTone = "Analytical, crisp, and direct",
        iconName = "Mic"
    )
}

data class EmotionAnalysisResult(
    val emotion: VenusEmotion,
    val confidence: Float,
    val acousticEnergy: Float,
    val sentimentScore: Float,
    val suggestion: String
)

object VenusEmotionDetector {

    private val HAPPY_KEYWORDS = listOf(
        "happy", "great", "awesome", "good", "love", "wonderful", "fantastic", "amazing",
        "yay", "glad", "delighted", "super", "brilliant", "perfect", "excited", "celebrate"
    )

    private val STRESSED_KEYWORDS = listOf(
        "urgent", "panic", "stressed", "help", "emergency", "hurry", "asap", "broken",
        "fail", "angry", "annoyed", "frustrated", "deadline", "late", "terrible", "overwhelmed"
    )

    private val SAD_KEYWORDS = listOf(
        "sad", "depressed", "tired", "exhausted", "lonely", "hurt", "bad", "unhappy",
        "crying", "lost", "grief", "hopeless", "down", "disappointed", "bored"
    )

    private val EXCITED_KEYWORDS = listOf(
        "wow", "incredible", "can't wait", "let's go", "bingo", "yes", "hyped",
        "omg", "insane", "fast", "unbelievable", "huge", "fire"
    )

    private val CALM_KEYWORDS = listOf(
        "peaceful", "relax", "meditate", "quiet", "zen", "serene", "slowly",
        "comfortable", "rest", "sleep", "chill", "steady"
    )

    /**
     * Estimates user emotion by blending acoustic telemetry (RMS volume variance, speed)
     * and lexical sentiment parsing.
     */
    fun analyze(
        transcript: String,
        rmsVariance: Float = 0f, // 0.0 to 1.0 based on microphone speech fluctuations
        speechDurationSeconds: Float = 2.0f
    ): EmotionAnalysisResult {
        val lower = transcript.lowercase(Locale.ROOT)
        val wordCount = transcript.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val wordsPerSecond = if (speechDurationSeconds > 0.5f) wordCount / speechDurationSeconds else 2.5f

        var happyScore = 0f
        var stressedScore = 0f
        var sadScore = 0f
        var excitedScore = 0f
        var calmScore = 0f

        // Lexical token matching
        HAPPY_KEYWORDS.forEach { if (lower.contains(it)) happyScore += 1.5f }
        STRESSED_KEYWORDS.forEach { if (lower.contains(it)) stressedScore += 1.8f }
        SAD_KEYWORDS.forEach { if (lower.contains(it)) sadScore += 1.8f }
        EXCITED_KEYWORDS.forEach { if (lower.contains(it)) excitedScore += 1.6f }
        CALM_KEYWORDS.forEach { if (lower.contains(it)) calmScore += 1.4f }

        // Acoustic telemetry blending
        if (rmsVariance > 0.6f && wordsPerSecond > 3.2f) {
            excitedScore += 1.5f
            stressedScore += 1.0f
        } else if (rmsVariance > 0.7f && stressedScore > 0) {
            stressedScore += 2.0f
        } else if (rmsVariance < 0.25f && wordsPerSecond < 2.0f) {
            sadScore += 1.2f
            calmScore += 1.0f
        } else if (rmsVariance in 0.25f..0.55f && wordsPerSecond in 2.0f..3.0f) {
            calmScore += 1.2f
        }

        // Punctuation clues
        if (transcript.contains("!")) excitedScore += 1.0f
        if (transcript.contains("?!") || transcript.contains("!!")) stressedScore += 1.2f

        val scores = listOf(
            VenusEmotion.HAPPY to happyScore,
            VenusEmotion.STRESSED to stressedScore,
            VenusEmotion.SAD to sadScore,
            VenusEmotion.EXCITED to excitedScore,
            VenusEmotion.CALM to calmScore
        )

        val maxEntry = scores.maxByOrNull { it.second } ?: (VenusEmotion.NEUTRAL to 0f)

        val detectedEmotion = if (maxEntry.second >= 1.0f) {
            maxEntry.first
        } else {
            VenusEmotion.NEUTRAL
        }

        val confidence = when {
            maxEntry.second >= 3.0f -> 0.94f
            maxEntry.second >= 2.0f -> 0.85f
            maxEntry.second >= 1.0f -> 0.72f
            else -> 0.60f
        }

        val suggestion = when (detectedEmotion) {
            VenusEmotion.STRESSED -> "I sense urgency in your voice. Take a deep breath — I'm right here to take care of this."
            VenusEmotion.SAD -> "You sound a bit weary. Remember to be kind to yourself; I'm here to help ease your day."
            VenusEmotion.HAPPY -> "I love the positive energy in your voice! Let's keep this great momentum going."
            VenusEmotion.EXCITED -> "High energy detected! Ready to execute any command at maximum speed."
            VenusEmotion.CALM -> "Smooth, tranquil vocal state. Optimal focus parameters active."
            VenusEmotion.NEUTRAL -> "Clear vocal transmission. All V.E.N.U.S systems standing by."
        }

        return EmotionAnalysisResult(
            emotion = detectedEmotion,
            confidence = confidence,
            acousticEnergy = rmsVariance.coerceIn(0f, 1f),
            sentimentScore = (happyScore + excitedScore - stressedScore - sadScore).coerceIn(-5f, 5f),
            suggestion = suggestion
        )
    }
}
