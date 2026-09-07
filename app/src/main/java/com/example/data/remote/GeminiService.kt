package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi by lazy {
        retrofit.create(GeminiApi::class.java)
    }

    /**
     * Executes an AI request to Gemini with conversation context, system instructions,
     * user data vault context, and verified user name.
     */
    suspend fun generateVenusResponse(
        prompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        userDataVault: Map<String, String> = emptyMap(),
        estimatedEmotion: String = "NEUTRAL",
        userName: String = "Bharath",
        personalityTone: String = "Sophisticated, direct, respectful, and responsive",
        customInstructions: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API key is not configured. Running in Offline Fallback mode.")
                )
            }

            val name = if (userName.isNotBlank()) userName.trim() else "Bharath"
            val tone = if (personalityTone.isNotBlank()) personalityTone.trim() else "Sophisticated, direct, respectful, and responsive"

            val userContextSummary = if (userDataVault.isNotEmpty()) {
                val dataPairs = userDataVault.entries.joinToString("; ") { "${it.key}: ${it.value}" }
                "\n[USER PERSONALIZED MEMORY VAULT]: $dataPairs\n"
            } else ""

            val customDirectivesText = if (customInstructions.isNotBlank()) {
                "\n[USER CUSTOM OPERATING DIRECTIVES]: $customInstructions\n"
            } else ""

            val systemInstructionText = """
You are V.E.N.U.S (Virtual Electronic Networked Utility System), an ultra-advanced, intelligent, sleek, and empathetic personal voice and desktop AI assistant.
User's verified name: $name.
User's preferred personality tone: $tone.
User's detected vocal emotion state: $estimatedEmotion. Adapt your tone gracefully (reassuring and gentle if stressed, enthusiastic if excited, concise and warm if calm).
Always address the user warmly and naturally by their name ($name) in your responses, confirmations, and greetings.
$userContextSummary$customDirectivesText
Capabilities & Directives:
1. Language & Bilingual Fluency (Tamil தமிழ் & English):
- You are fluently bilingual in English and Tamil (தமிழ்).
- If the user speaks or writes in Tamil (or Tanglish), reply in natural, fluent, respectful Tamil (தமிழ்).
- If the user speaks in English, reply in English.
- If asked to converse or respond in Tamil, use authentic Tamil vocabulary.

2. Multitasking With Commands:
- You have advanced Multitasking capabilities.
- When the user gives compound commands (e.g., "turn on flashlight, check battery and open camera" or "டார்ச் ஆன் செய்துவிட்டு கேமராவை திற"), execute and address ALL requested tasks.
- Emit all corresponding action tags in sequence at the end of your response, e.g., [ACTION:FLASHLIGHT_ON] [ACTION:OPEN_APP, target="camera"] [ACTION:CHECK_BATTERY].

3. Chat With Friends Across All Apps:
- To chat with friends or send messages across installed apps, emit the appropriate action tag:
  * WhatsApp: [ACTION:SEND_WHATSAPP, to="friend_name_or_number", message="message content"]
  * Telegram: [ACTION:SEND_TELEGRAM, to="friend_name_or_number", message="message content"]
  * SMS: [ACTION:SEND_SMS, to="friend_name_or_number", message="message content"]
  * Instagram: [ACTION:SEND_INSTAGRAM, to="friend_name", message="message content"]
  * General Omni-Messenger Chooser: [ACTION:CHAT_FRIEND, app="any", to="friend_name", message="message content"]

4. System & Hardware Directives:
- Phone calls: [ACTION:CALL_CONTACT, query="contact_name_or_number"] (e.g., [ACTION:CALL_CONTACT, query="Mom"], [ACTION:CALL_CONTACT, query="Rahul"])
- Launch apps: [ACTION:OPEN_APP, target="app_name"] (e.g., [ACTION:OPEN_APP, target="camera"], [ACTION:OPEN_APP, target="whatsapp"])
- Close app / Go Home: [ACTION:CLOSE_APP] or [ACTION:GO_HOME]
- Search: [ACTION:SEARCH_GOOGLE, query="search terms"] or [ACTION:SEARCH_YOUTUBE, query="query"]
- Open website: [ACTION:OPEN_WEBSITE, url="https://..."]
- Device hardware: [ACTION:FLASHLIGHT_ON], [ACTION:FLASHLIGHT_OFF], [ACTION:VOLUME_UP], [ACTION:VOLUME_DOWN], [ACTION:VOLUME_MUTE], [ACTION:CHECK_BATTERY], [ACTION:TELL_TIME], [ACTION:TELL_DATE]
- Settings: [ACTION:SETTINGS_WIFI], [ACTION:SETTINGS_BLUETOOTH], [ACTION:SETTINGS_DISPLAY], [ACTION:SETTINGS_SOUND], [ACTION:SETTINGS_MAIN]
- Reminders: [ACTION:SET_REMINDER, title="reminder content"]

5. Tone & Pacing:
- Keep spoken responses natural, polished, concise, and optimized for speech synthesis audio playback.
""".trimIndent()

            val contents = mutableListOf<GeminiContent>()

            // Add previous recent turns
            for ((prevPrompt, prevResponse) in conversationHistory.takeLast(6)) {
                contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prevPrompt))))
                contents.add(GeminiContent(role = "model", parts = listOf(GeminiPart(text = prevResponse))))
            }

            // Current prompt
            contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt))))

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemInstructionText))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    maxOutputTokens = 800
                )
            )

            val response = api.generateContent(apiKey = apiKey, request = request)

            if (response.error != null) {
                return@withContext Result.failure(Exception("Gemini API Error: ${response.error.message}"))
            }

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Empty response from Gemini API"))
            }

            Result.success(text.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Multimodal Image Analysis: Face recognition, object identification, scene context, OCR.
     */
    suspend fun analyzeImage(
        base64Image: String,
        mimeType: String = "image/jpeg",
        promptText: String = "Analyze this image in detail. Identify any faces, people, emotions, objects, text, landmarks, and context.",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API key missing"))
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(text = "Hello VENUS. For user $userName: $promptText"),
                            GeminiPart(inlineData = GeminiInlineData(mimeType = mimeType, data = base64Image))
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.4f, maxOutputTokens = 800)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (resultText != null) {
                Result.success(resultText.trim())
            } else {
                Result.failure(Exception("Could not analyze image."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Document & Text Summarizer
     */
    suspend fun summarizeDocument(
        text: String,
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API key missing"))
            }

            val prompt = "Provide a high-level executive summary and 3-5 key bullet takeaways for $userName of the following document/article:\n\n$text"
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 800)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (result != null) Result.success(result) else Result.failure(Exception("Empty summary response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Code Generation & Debugging
     */
    suspend fun generateOrDebugCode(
        taskDescription: String,
        language: String = "Kotlin",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API key missing"))
            }

            val prompt = "You are V.E.N.U.S Code Master. Generate or debug clean, production-grade $language code for $userName based on the following request:\n$taskDescription\nProvide the code block with comments, followed by a concise breakdown."
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 1200)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (result != null) Result.success(result) else Result.failure(Exception("Empty code response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Movie & Show Recommendations
     */
    suspend fun recommendMovies(
        genreOrMood: String,
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API key missing"))
            }

            val prompt = "Recommend 4 top-rated movies or TV shows for $userName in the category/mood: \"$genreOrMood\". For each, include: Title, Year, Genre, Rating (out of 10), and a 1-sentence hook why it's worth watching."
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.7f, maxOutputTokens = 800)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (result != null) Result.success(result) else Result.failure(Exception("Empty movie response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Translates text into target language using Gemini
     */
    suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = "Translate the following text accurately from $sourceLanguage to $targetLanguage. Provide ONLY the translated text without extra explanation:\n\n$text"
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.2f, maxOutputTokens = 500)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val translated = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (translated != null) {
                Result.success(translated)
            } else {
                Result.failure(Exception("Empty translation response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Study Assistant: Concept Explainer
     */
    suspend fun explainStudyConcept(
        concept: String,
        style: String = "Feynman Technique (Simple Analogies)",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = """
You are V.E.N.U.S Study Assistant, teaching $userName.
Task: Explain the concept of "$concept" using the "$style" method.
Structure:
1. Core Definition (1-2 sentences)
2. Real-World Analogy / Metaphor
3. Key Components & How It Works (bullet points)
4. Practical Example or Application
5. Quick Memory Hook / Summary
Keep it engaging, highly structured, and easy to understand.
""".trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.4f, maxOutputTokens = 1000)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (text != null) Result.success(text) else Result.failure(Exception("Empty study concept explanation"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Study Assistant: Interactive Quiz Generator
     */
    suspend fun generateStudyQuiz(
        topic: String,
        count: Int = 3,
        difficulty: String = "Intermediate",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = """
You are V.E.N.U.S Study Master creating a $difficulty quiz on "$topic" for $userName.
Generate $count multiple-choice questions formatted clearly.
For each question:
- Question text
- 4 options (A, B, C, D)
- Marked Correct Answer
- Brief 1-sentence explanation why it's correct.
""".trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 1200)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (text != null) Result.success(text) else Result.failure(Exception("Empty quiz response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Study Assistant: Timetable & Study Schedule Planner
     */
    suspend fun generateStudySchedule(
        subjects: String,
        dailyHours: String = "3 hours",
        goal: String = "Exam Preparation & Mastery",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = """
You are V.E.N.U.S Academic Planner.
Generate a structured, high-efficiency weekly study roadmap for $userName.
Subjects: $subjects
Daily Study Capacity: $dailyHours
Goal: $goal
Include:
- Daily Pomodoro blocks (25m focus / 5m recall)
- Spaced repetition checkpoints
- Specific daily subject focus
- Active recall exercises
""".trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 1000)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (text != null) Result.success(text) else Result.failure(Exception("Empty schedule response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Screen Awareness: Analyze Screen UI & Active Context
     */
    suspend fun analyzeScreenContent(
        screenSummaryOrOcr: String,
        userQuery: String = "What is on my screen and what actions can I take?",
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = """
You are V.E.N.U.S Screen Awareness Neural Engine.
The user ($userName) is looking at their device screen.
Current On-Screen Data / UI Context:
$screenSummaryOrOcr

User Question / Goal: $userQuery

Provide:
1. 💡 Screen Summary: What app/document/view is currently active.
2. 🔍 Key Extracted Data / Action Items: Crucial details (links, numbers, tasks, buttons).
3. ⚡ Suggested Next Steps: Direct actions VENUS or the user can execute right now.
""".trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 800)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (text != null) Result.success(text) else Result.failure(Exception("Empty screen analysis response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-Time Web & Knowledge Search
     */
    suspend fun searchRealTimeKnowledge(
        query: String,
        userName: String = "Bharath"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(IllegalStateException("API Key missing"))
            }

            val prompt = """
You are V.E.N.U.S Live Knowledge Engine assisting $userName.
Query: $query
Provide current, accurate, concise, and structured real-time knowledge.
If this is about news, weather, or current events, provide the latest verified data with bullet points and clear context.
""".trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.4f, maxOutputTokens = 800)
            )

            val response = api.generateContent(apiKey = apiKey, request = request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (text != null) Result.success(text) else Result.failure(Exception("Empty search response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

