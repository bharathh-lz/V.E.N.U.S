package com.example.data.local

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OfflineIntentResult(
    val response: String,
    val actionType: String? = null,
    val actionPayload: String? = null,
    val detectedEmotion: String = "NEUTRAL"
)

data class MultitaskOfflineResult(
    val isMultitask: Boolean,
    val combinedResponse: String,
    val actions: List<Pair<String, String?>>, // (actionType, payload)
    val detectedEmotion: String = "HAPPY"
)

object OfflineKnowledgeBase {

    fun getGreetings(userName: String): List<String> = listOf(
        "Hello $userName! V.E.N.U.S offline intelligence core is active and ready to assist you.",
        "Greetings $userName! All local systems operational. How may I assist you today?",
        "Systems online, $userName. I am listening and ready for your command.",
        "Good day $userName! I am ready to help you with tasks, knowledge, and system controls."
    )

    fun getTamilGreetings(userName: String): List<String> = listOf(
        "வணக்கம் $userName! V.E.N.U.S உங்கள் குரல் உதவியாளர் தயாராக உள்ளது. என்ன செய்ய வேண்டும்?",
        "வணக்கம் $userName! அனைத்து உள்ளமைவு அமைப்புகளும் இயங்குகின்றன. நான் உங்களுக்கு உதவத் தயாராக உள்ளேன்.",
        "ஹே $userName! V.E.N.U.S பல்பணி உதவியாளர் தயார். உங்கள் கட்டளைகளைக் கூறுங்கள்."
    )

    private val JOKES = listOf(
        "Why did the neural network go to school? Because it wanted to improve its deep learning!",
        "There are 10 types of people in the world: those who understand binary, and those who don't.",
        "Why do programmers prefer dark mode? Because light attracts bugs!",
        "Why was the computer cold? It left its Windows open!",
        "What do you call an AI that sings? An algo-rhythm!"
    )

    private val TAMIL_JOKES = listOf(
        "கணினி ஏன் குளிர்ச்சியாக இருந்தது? ஏனென்றால் அது Windows திறந்து வைத்திருந்தது!",
        "நிரலாளர்கள் ஏன் இருண்ட பயன்முறையை (Dark Mode) விரும்புகிறார்கள்? வெளிச்சம் பிழைகளை (Bugs) ஈர்க்கும் என்பதால்!"
    )

    private val MOTIVATIONS = listOf(
        "“The best way to predict the future is to create it.” Keep pushing forward!",
        "“Small daily improvements over time lead to stunning results.” You've got this!",
        "“Focus on being productive instead of busy.” Let's accomplish great things today!",
        "“Your time is limited, don't waste it living someone else's life.” Make today count!"
    )

    private val TAMIL_MOTIVATIONS = listOf(
        "“எதிர்காலத்தை கணிக்க சிறந்த வழி அதை உருவாக்குவதே.” தொடர்ந்து முன்னேறுங்கள்!",
        "“முயற்சி திருவினையாக்கும்.” இன்றைய நாள் உங்களுக்கு சிறந்த வெற்றியைத் தரும்!"
    )

    private val FACTS = mapOf(
        "who are you" to "I am V.E.N.U.S — Virtual Electronic Networked Utility System. Your private, high-performance voice and desktop assistant.",
        "what does venus stand for" to "V.E.N.U.S stands for Virtual Electronic Networked Utility System — engineered for total privacy, seamless automation, and neural assistance.",
        "who created you" to "I was engineered as the Project V.E.N.U.S personal artificial intelligence system.",
        "how do you work" to "I process acoustic voice signals, detect speech cadence & emotions, execute local system automations, and query encrypted local storage with optional Gemini cloud neural processing.",
        "speed of light" to "The speed of light in vacuum is exactly 299,792,458 meters per second (approximately 300,000 km/s or 186,282 miles/s).",
        "distance to moon" to "The average distance from the Earth to the Moon is approximately 384,400 kilometers (238,855 miles).",
        "planet venus" to "Venus is the second planet from the Sun. It has a thick toxic atmosphere composed mainly of carbon dioxide with clouds of sulfuric acid, making it the hottest planet in our solar system.",
        "largest ocean" to "The Pacific Ocean is the largest and deepest ocean on Earth, covering more than 63 million square miles.",
        "capital of france" to "The capital of France is Paris.",
        "capital of japan" to "The capital of Japan is Tokyo.",
        "capital of usa" to "The capital of the United States is Washington, D.C.",
        "capital of germany" to "The capital of Germany is Berlin.",
        "capital of canada" to "The capital of Canada is Ottawa.",
        "capital of india" to "The capital of India is New Delhi.",
        "capital of australia" to "The capital of Australia is Canberra."
    )

    /**
     * Splits compound input into discrete sub-commands for Multitasking.
     */
    fun splitMultitaskCommands(input: String): List<String> {
        val delimiters = listOf(
            " and then ", " and also ", " as well as ", " and ", " then ", " also ", ";",
            " மற்றும் பிறகு ", " மற்றும் ", " பிறகு ", " அப்புறம் ", " செய்துவிட்டு ", " செய்து "
        )

        var segments = listOf(input.trim())
        for (delimiter in delimiters) {
            val nextList = mutableListOf<String>()
            for (seg in segments) {
                if (seg.contains(delimiter, ignoreCase = true)) {
                    val parts = seg.split(Regex(Regex.escape(delimiter), RegexOption.IGNORE_CASE))
                    nextList.addAll(parts.map { it.trim() }.filter { it.isNotBlank() })
                } else {
                    nextList.add(seg)
                }
            }
            segments = nextList
        }
        return segments.filter { it.length > 2 }
    }

    /**
     * Multitasking Processor: Evaluates multiple sub-intents in a single pass.
     */
    fun processMultitaskInput(
        input: String,
        userMemory: Map<String, String> = emptyMap(),
        userName: String = "Bharath"
    ): MultitaskOfflineResult {
        val subCommands = splitMultitaskCommands(input)
        val isTamil = input.any { it in '\u0B80'..'\u0BFF' }

        if (subCommands.size <= 1) {
            val single = processOfflineInput(input, userMemory, userName)
            return MultitaskOfflineResult(
                isMultitask = false,
                combinedResponse = single.response,
                actions = if (single.actionType != null) listOf(Pair(single.actionType, single.actionPayload)) else emptyList(),
                detectedEmotion = single.detectedEmotion
            )
        }

        val actionList = mutableListOf<Pair<String, String?>>()
        val responseBullets = mutableListOf<String>()

        subCommands.forEachIndexed { index, subCmd ->
            val result = processOfflineInput(subCmd, userMemory, userName)
            if (result.actionType != null) {
                actionList.add(Pair(result.actionType, result.actionPayload))
            }
            responseBullets.add("${index + 1}. ${result.response}")
        }

        val header = if (isTamil) {
            "பல்பணி கட்டளைகள் வெற்றிகரமாக செயல்படுத்தப்பட்டன (${subCommands.size} பணிகள்):"
        } else {
            "Multitasking sequence completed (${subCommands.size} tasks):"
        }

        val combined = "$header\n" + responseBullets.joinToString("\n")

        return MultitaskOfflineResult(
            isMultitask = true,
            combinedResponse = combined,
            actions = actionList,
            detectedEmotion = "HAPPY"
        )
    }

    /**
     * Evaluates user input against offline rules, system actions, math, units, facts, and Tamil commands.
     */
    fun processOfflineInput(
        input: String,
        userMemory: Map<String, String> = emptyMap(),
        userName: String = "Bharath"
    ): OfflineIntentResult {
        val clean = input.trim().lowercase(Locale.ROOT)
        val name = if (userName.isNotBlank()) userName.trim() else "Bharath"
        val isTamil = input.any { it in '\u0B80'..'\u0BFF' }

        // 1. Math evaluation (e.g., "what is 45 * 12", "calculate 100 / 4", "15 + 85")
        val mathResult = tryEvaluateMath(clean)
        if (mathResult != null) {
            return OfflineIntentResult(
                response = if (isTamil) "$name, கணக்கீட்டின் விடை: $mathResult." else "$name, the calculation result is $mathResult.",
                detectedEmotion = "CALM"
            )
        }

        // 2. Unit conversion
        val unitResult = tryUnitConversion(clean)
        if (unitResult != null) {
            return OfflineIntentResult(
                response = "$name, $unitResult",
                detectedEmotion = "CALM"
            )
        }

        // 3. User personalized memory queries
        for ((label, value) in userMemory) {
            val labelClean = label.lowercase(Locale.ROOT)
            if (clean.contains("what is my $labelClean") ||
                clean.contains("who is my $labelClean") ||
                clean.contains("my $labelClean") ||
                (labelClean.contains("name") && (clean.contains("my name") || clean.contains("என் பெயர்"))) ||
                (labelClean.contains("job") && (clean.contains("my job") || clean.contains("என் வேலை")))
            ) {
                return OfflineIntentResult(
                    response = if (isTamil) "$name, உங்கள் பாதுகாக்கப்பட்ட நினைவகத்தின்படி, $label: $value." else "$name, according to your encrypted memory vault, your $label is: $value.",
                    detectedEmotion = "HAPPY"
                )
            }
        }

        // 4. Time & Date queries (English & Tamil)
        if ((clean.contains("time") && (clean.contains("what") || clean.contains("tell") || clean.contains("current"))) ||
            clean.contains("நேரம்") || clean.contains("மணி என்ன")
        ) {
            val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            return OfflineIntentResult(
                response = if (isTamil) "$name, தற்போதைய நேரம் $time." else "$name, the current time is $time.",
                actionType = "TELL_TIME",
                detectedEmotion = "CALM"
            )
        }

        if (clean.contains("date") || clean.contains("today's date") || clean.contains("day is it") ||
            clean.contains("தேதி") || clean.contains("இன்று என்ன நாள்") || clean.contains("கிழமை")
        ) {
            val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
            return OfflineIntentResult(
                response = if (isTamil) "$name, இன்று $date." else "$name, today is $date.",
                actionType = "TELL_DATE",
                detectedEmotion = "CALM"
            )
        }

        // 5. Cross-App Friend Chat & Messaging (English & Tamil)
        if (clean.contains("whatsapp") || clean.contains("வாட்ஸ்அப்")) {
            val to = extractRecipient(clean)
            val msg = extractMessageBody(clean)
            return OfflineIntentResult(
                response = if (isTamil) "நண்பருக்கு வாட்ஸ்அப் வழியாக செய்தி அனுப்புகிறேன், $name." else "Preparing WhatsApp chat for $to, $name.",
                actionType = "SEND_WHATSAPP",
                actionPayload = "to=\"$to\", message=\"$msg\"",
                detectedEmotion = "HAPPY"
            )
        }

        if (clean.contains("telegram") || clean.contains("டெலிகிராம்")) {
            val to = extractRecipient(clean)
            val msg = extractMessageBody(clean)
            return OfflineIntentResult(
                response = if (isTamil) "டெலிகிராம் வழியாக செய்தி தயார் செய்கிறேன், $name." else "Preparing Telegram chat for $to, $name.",
                actionType = "SEND_TELEGRAM",
                actionPayload = "to=\"$to\", message=\"$msg\"",
                detectedEmotion = "HAPPY"
            )
        }

        if (clean.contains("chat with friend") || clean.contains("message friend") || clean.contains("send message") ||
            clean.contains("நண்பருக்கு செய்தி") || clean.contains("மெசேஜ் அனுப்பு") || clean.contains("செய்தி அனுப்பு")
        ) {
            val to = extractRecipient(clean)
            val msg = extractMessageBody(clean)
            return OfflineIntentResult(
                response = if (isTamil) "அனைத்து செயலிகள் வழியாக நண்பருக்கு செய்தி அனுப்ப தயார் செய்கிறேன், $name." else "Launching omni-messenger chat for $to across your apps, $name.",
                actionType = "CHAT_FRIEND",
                actionPayload = "to=\"$to\", message=\"$msg\"",
                detectedEmotion = "HAPPY"
            )
        }

        // 6. Phone Calls & Contacts (English & Tamil)
        if (clean.startsWith("call ") || clean.startsWith("phone ") || clean.startsWith("dial ") ||
            clean.startsWith("make a call to ") || clean.startsWith("place a call to ") || clean.startsWith("please call ") ||
            clean.contains("அழைப்பு செய்") || clean.contains("கால் செய்")
        ) {
            val contactTarget = clean
                .removePrefix("make a call to ")
                .removePrefix("place a call to ")
                .removePrefix("please call ")
                .removePrefix("call ")
                .removePrefix("phone ")
                .removePrefix("dial ")
                .removePrefix("அழைப்பு செய் ")
                .removePrefix("கால் செய் ")
                .trim()

            return OfflineIntentResult(
                response = if (isTamil) "$contactTarget என்பவரைத் தொடர்புகொள்கிறேன், $name." else "Accessing your contacts and calling $contactTarget for you, $name.",
                actionType = "CALL_CONTACT",
                actionPayload = contactTarget,
                detectedEmotion = "EXCITED"
            )
        }

        // 7. Flashlight / Torch (English & Tamil)
        if (clean.contains("flashlight on") || clean.contains("turn on flashlight") || clean.contains("torch on") ||
            clean.contains("டார்ச் ஆன்") || clean.contains("விளக்கு போடு") || clean.contains("டார்ச் போடு")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "டார்ச் ஆன் செய்யப்படுகிறது, $name." else "Turning on flashlight for you, $name.",
                actionType = "FLASHLIGHT_ON",
                detectedEmotion = "CALM"
            )
        }
        if (clean.contains("flashlight off") || clean.contains("turn off flashlight") || clean.contains("torch off") ||
            clean.contains("டார்ச் ஆஃப்") || clean.contains("விளக்கு அணை") || clean.contains("டார்ச் அணை")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "டார்ச் அணைக்கப்படுகிறது, $name." else "Turning off flashlight, $name.",
                actionType = "FLASHLIGHT_OFF",
                detectedEmotion = "CALM"
            )
        }

        // 8. Volume Controls (English & Tamil)
        if (clean.contains("volume up") || clean.contains("increase volume") || clean.contains("louder") ||
            clean.contains("வால்யூம் கூட்டு") || clean.contains("சத்தம் ஏற்று") || clean.contains("வால்யூம் ஏற்று")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "வால்யூம் அதிகரிக்கப்படுகிறது, $name." else "Increasing volume, $name.",
                actionType = "VOLUME_UP",
                detectedEmotion = "CALM"
            )
        }
        if (clean.contains("volume down") || clean.contains("decrease volume") || clean.contains("lower volume") || clean.contains("quieter") ||
            clean.contains("வால்யூம் குறை") || clean.contains("சத்தம் குறை")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "வால்யூம் குறைக்கப்படுகிறது, $name." else "Decreasing volume, $name.",
                actionType = "VOLUME_DOWN",
                detectedEmotion = "CALM"
            )
        }
        if (clean.contains("mute") || clean.contains("silence") || clean.contains("மியூட்") || clean.contains("அமைதிப்படுத்து")) {
            return OfflineIntentResult(
                response = if (isTamil) "ஒலி அணைக்கப்படுகிறது (Mute), $name." else "Muting audio stream, $name.",
                actionType = "VOLUME_MUTE",
                detectedEmotion = "CALM"
            )
        }

        // 9. Battery Status (English & Tamil)
        if (clean.contains("battery") || clean.contains("power level") || clean.contains("charge percentage") ||
            clean.contains("பேட்டரி") || clean.contains("சார்ஜ் எவ்வளவு") || clean.contains("மின் நிலை")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "பேட்டரி அளவை சரிபார்க்கிறேன், $name..." else "Checking power level for you, $name...",
                actionType = "CHECK_BATTERY",
                detectedEmotion = "CALM"
            )
        }

        // 10. Websites & Web navigation
        if (clean.startsWith("open website ") || clean.startsWith("browse to ") || clean.startsWith("go to ") ||
            (clean.startsWith("open ") && (clean.contains(".com") || clean.contains(".org") || clean.contains(".net") || clean.contains(".io") || clean.contains("http")))
        ) {
            val site = clean
                .removePrefix("open website ")
                .removePrefix("browse to ")
                .removePrefix("go to ")
                .removePrefix("open ")
                .trim()
            return OfflineIntentResult(
                response = "Opening $site for you, $name.",
                actionType = "OPEN_WEBSITE",
                actionPayload = site,
                detectedEmotion = "EXCITED"
            )
        }

        // Close App / Go Home
        if (clean == "close app" || clean == "exit app" || clean == "go home" || clean == "close this" || clean == "minimize" ||
            clean.contains("முகப்பு செல்") || clean.contains("வெளியேறு")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "முகப்புத் திரைக்கு செல்கிறேன், $name." else "Navigating to home screen, $name.",
                actionType = "CLOSE_APP",
                detectedEmotion = "CALM"
            )
        }

        // Play Music
        if (clean.startsWith("play music") || clean.startsWith("play song") || clean.startsWith("play track") ||
            clean.contains("பாட்டு போடு") || clean.contains("பாடல் இசைக்க")
        ) {
            val query = clean
                .removePrefix("play music ")
                .removePrefix("play song ")
                .removePrefix("play track ")
                .removePrefix("play ")
                .removePrefix("பாட்டு போடு ")
                .trim()
            return OfflineIntentResult(
                response = if (isTamil) "இசையை இயக்குகிறேன், $name." else "Starting music playback for you, $name.",
                actionType = "PLAY_MUSIC",
                actionPayload = query,
                detectedEmotion = "EXCITED"
            )
        }

        // System Actions: Open Apps (English & Tamil)
        if (clean.startsWith("open ") || clean.startsWith("launch ") || clean.startsWith("start ") ||
            clean.contains("திற") || clean.contains("திறக்கவும்")
        ) {
            val appTarget = clean
                .removePrefix("open ")
                .removePrefix("launch ")
                .removePrefix("start ")
                .removeSuffix(" திற")
                .removeSuffix(" திறக்கவும்")
                .removeSuffix(" app")
                .trim()

            return OfflineIntentResult(
                response = if (isTamil) "$appTarget செயலியைத் திறக்கிறேன், $name." else "Launching $appTarget for you, $name.",
                actionType = "OPEN_APP",
                actionPayload = appTarget,
                detectedEmotion = "EXCITED"
            )
        }

        // Search Google / YouTube
        if (clean.startsWith("search youtube for ") || clean.startsWith("youtube ")) {
            val query = clean.removePrefix("search youtube for ").removePrefix("youtube ").trim()
            return OfflineIntentResult(
                response = "Searching YouTube for $query, $name.",
                actionType = "SEARCH_YOUTUBE",
                actionPayload = query,
                detectedEmotion = "EXCITED"
            )
        }

        if (clean.startsWith("search google for ") || clean.startsWith("search for ") || clean.startsWith("google ")) {
            val query = clean
                .removePrefix("search google for ")
                .removePrefix("search for ")
                .removePrefix("google ")
                .trim()
            return OfflineIntentResult(
                response = "Searching Google for $query, $name.",
                actionType = "SEARCH_GOOGLE",
                actionPayload = query,
                detectedEmotion = "EXCITED"
            )
        }

        // Settings Shortcuts
        if (clean.contains("wifi settings") || clean.contains("open wifi") || clean.contains("வைஃபை")) {
            return OfflineIntentResult(
                response = if (isTamil) "வைஃபை அமைப்புகளைத் திறக்கிறேன், $name." else "Opening Wi-Fi configuration for you, $name.",
                actionType = "SETTINGS_WIFI"
            )
        }
        if (clean.contains("bluetooth settings") || clean.contains("open bluetooth") || clean.contains("ப்ளூடூத்")) {
            return OfflineIntentResult(
                response = if (isTamil) "ப்ளூடூத் அமைப்புகளைத் திறக்கிறேன், $name." else "Opening Bluetooth configuration, $name.",
                actionType = "SETTINGS_BLUETOOTH"
            )
        }

        // Reminders
        if (clean.startsWith("remind me to ") || clean.startsWith("set reminder for ") || clean.startsWith("add reminder ") ||
            clean.contains("நினைவூட்டல்")
        ) {
            val reminderText = clean
                .removePrefix("remind me to ")
                .removePrefix("set reminder for ")
                .removePrefix("add reminder ")
                .removePrefix("நினைவூட்டல் அமை ")
                .trim()

            return OfflineIntentResult(
                response = if (isTamil) "உங்களுக்காக நினைவூட்டல் பதிவு செய்யப்பட்டது, $name: \"$reminderText\"." else "I have scheduled a reminder for you, $name: \"$reminderText\".",
                actionType = "SET_REMINDER",
                actionPayload = reminderText,
                detectedEmotion = "HAPPY"
            )
        }

        // Facts & Knowledge Base
        for ((q, ans) in FACTS) {
            if (clean.contains(q)) {
                return OfflineIntentResult(
                    response = "$name, $ans",
                    detectedEmotion = "CALM"
                )
            }
        }

        // Jokes & Fun
        if (clean.contains("joke") || clean.contains("make me laugh") || clean.contains("funny") ||
            clean.contains("நகைச்சுவை") || clean.contains("கதை")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "$name, இதோ ஒரு நகைச்சுவை: ${TAMIL_JOKES.random()}" else "$name, here's one: ${JOKES.random()}",
                detectedEmotion = "HAPPY"
            )
        }

        // Motivation
        if (clean.contains("motivate") || clean.contains("inspire") || clean.contains("quote") || clean.contains("tired") || clean.contains("sad") ||
            clean.contains("ஊக்கம்") || clean.contains("பொன்மொழி")
        ) {
            return OfflineIntentResult(
                response = if (isTamil) "$name, இதோ ஒரு ஊக்கமளிக்கும் சிந்தனை: ${TAMIL_MOTIVATIONS.random()}" else "Here is some inspiration for you, $name: ${MOTIVATIONS.random()}",
                detectedEmotion = "HAPPY"
            )
        }

        // Responsiveness & Command Acknowledgment
        if (clean.contains("respond to my command") || clean.contains("respond to command") || clean.contains("respond to me") || clean == "respond" || clean.contains("respond please")) {
            return OfflineIntentResult(
                response = if (isTamil) "நான் உங்கள் கட்டளைகளுக்கு முழுமையாக பதிலளிக்கிறேன், கமாண்டர் $name. அமைப்புகள் அனைத்தும் தயார் நிலையில் உள்ளன. என்ன செய்ய வேண்டும்?" else "I am active, fully responsive, and executing your command, Commander $name. All systems are operational. What would you like me to do?",
                detectedEmotion = "HAPPY"
            )
        }

        if (clean.contains("can you hear me") || clean.contains("are you there") || clean.contains("are you listening") || clean.contains("are you online") || clean == "test" || clean == "testing") {
            return OfflineIntentResult(
                response = if (isTamil) "தெளிவாகக் கேட்கிறது, கமாண்டர் $name! V.E.N.U.S உங்கள் கட்டளைக்காகக் காத்திருக்கிறது." else "Loud and clear, Commander $name! V.E.N.U.S is active and listening for your orders.",
                detectedEmotion = "HAPPY"
            )
        }

        if (clean.contains("what can you do") || clean.contains("help") || clean.contains("features") || clean.contains("list commands")) {
            return OfflineIntentResult(
                response = "Commander $name, I can execute system commands like controlling flashlight and volume, opening applications, checking battery and time, solving math calculations, setting reminders, telling jokes, and providing answers to your questions.",
                detectedEmotion = "HAPPY"
            )
        }

        // Greetings (English & Tamil)
        if (clean.contains("வணக்கம்") || clean.contains("காலை வணக்கம்") || clean.contains("மாலை வணக்கம்") || clean.contains("ஹே வீனஸ்")) {
            return OfflineIntentResult(
                response = getTamilGreetings(name).random(),
                detectedEmotion = "HAPPY"
            )
        }

        if (clean.contains("hello") || clean.contains("hey") || clean.contains("hi venus") || clean.contains("good morning") || clean.contains("good evening")) {
            return OfflineIntentResult(
                response = getGreetings(name).random(),
                detectedEmotion = "HAPPY"
            )
        }

        // Fallback Intelligent Response
        return OfflineIntentResult(
            response = if (isTamil) {
                "கமாண்டர் $name, உங்கள் கட்டளை பெறப்பட்டது: \"$input\". நான் முழுமையாக பதிலளிக்கத் தயாராக உள்ளேன். சாதனக் கட்டுப்பாடுகள், ஆப்ஸ், கணிதக் கணக்கீடுகள் மற்றும் தகவல்களைக் கேட்கலாம்."
            } else {
                "Commander $name, command received: \"$input\". I am online and responsive to your requests. You can ask me to open apps, control system hardware, calculate, tell the time, or execute tasks."
            },
            detectedEmotion = "NEUTRAL"
        )
    }

    private fun extractRecipient(text: String): String {
        val clean = text.lowercase(Locale.ROOT)
        val patterns = listOf("to ", "with ", "for ", "என்பவருக்கு ", "நண்பர் ")
        for (pat in patterns) {
            if (clean.contains(pat)) {
                val after = clean.substringAfter(pat).trim()
                val recipientWord = after.split(" ", ",", "saying", "asking").firstOrNull() ?: ""
                if (recipientWord.isNotBlank()) return recipientWord
            }
        }
        return "Friend"
    }

    private fun extractMessageBody(text: String): String {
        val clean = text.lowercase(Locale.ROOT)
        val markers = listOf("saying ", "that ", "asking ", "message ", "செய்தி ")
        for (m in markers) {
            if (clean.contains(m)) {
                return text.substring(text.indexOf(m) + m.length).trim()
            }
        }
        return "Hello! How are you?"
    }

    private fun tryEvaluateMath(input: String): String? {
        val mathPattern = Regex("""(?:what\s+is\s+|calculate\s+|evaluate\s+)?(\d+(?:\.\d+)?)\s*([\+\-\*\/xX]|plus|minus|times|multiplied\s+by|divided\s+by)\s*(\d+(?:\.\d+)?)""")
        val match = mathPattern.find(input) ?: return null

        val (num1Str, opRaw, num2Str) = match.destructured
        val num1 = num1Str.toDoubleOrNull() ?: return null
        val num2 = num2Str.toDoubleOrNull() ?: return null

        val result = when {
            opRaw == "+" || opRaw == "plus" -> num1 + num2
            opRaw == "-" || opRaw == "minus" -> num1 - num2
            opRaw == "*" || opRaw == "x" || opRaw == "X" || opRaw.contains("times") || opRaw.contains("multiplied") -> num1 * num2
            opRaw == "/" || opRaw.contains("divided") -> if (num2 != 0.0) num1 / num2 else return "undefined (cannot divide by zero)"
            else -> return null
        }

        return if (result % 1.0 == 0.0) result.toLong().toString() else String.format(Locale.US, "%.2f", result)
    }

    private fun tryUnitConversion(input: String): String? {
        val milesToKm = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*miles?\s*(?:to|in)\s*(?:km|kilometers?)""").find(input)
        if (milesToKm != null) {
            val (valStr) = milesToKm.destructured
            val num = valStr.toDoubleOrNull() ?: return null
            val km = num * 1.60934
            return "$num miles is approximately ${String.format(Locale.US, "%.2f", km)} kilometers."
        }

        val kmToMiles = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:km|kilometers?)\s*(?:to|in)\s*miles?""").find(input)
        if (kmToMiles != null) {
            val (valStr) = kmToMiles.destructured
            val num = valStr.toDoubleOrNull() ?: return null
            val miles = num * 0.621371
            return "$num kilometers is approximately ${String.format(Locale.US, "%.2f", miles)} miles."
        }

        val cToF = Regex("""(?:convert\s+)?(-?\d+(?:\.\d+)?)\s*(?:c|celsius)\s*(?:to|in)\s*(?:f|fahrenheit)""").find(input)
        if (cToF != null) {
            val (valStr) = cToF.destructured
            val num = valStr.toDoubleOrNull() ?: return null
            val f = (num * 9.0 / 5.0) + 32.0
            return "$num°C is equal to ${String.format(Locale.US, "%.1f", f)}°F."
        }

        val fToC = Regex("""(?:convert\s+)?(-?\d+(?:\.\d+)?)\s*(?:f|fahrenheit)\s*(?:to|in)\s*(?:c|celsius)""").find(input)
        if (fToC != null) {
            val (valStr) = fToC.destructured
            val num = valStr.toDoubleOrNull() ?: return null
            val c = (num - 32.0) * 5.0 / 9.0
            return "$num°F is equal to ${String.format(Locale.US, "%.1f", c)}°C."
        }

        val kgToLbs = Regex("""(?:convert\s+)?(\d+(?:\.\d+)?)\s*(?:kg|kilograms?)\s*(?:to|in)\s*(?:lbs|pounds?)""").find(input)
        if (kgToLbs != null) {
            val (valStr) = kgToLbs.destructured
            val num = valStr.toDoubleOrNull() ?: return null
            val lbs = num * 2.20462
            return "$num kg is approximately ${String.format(Locale.US, "%.2f", lbs)} pounds."
        }

        return null
    }

    fun explainConceptOffline(concept: String): Result<String> {
        val clean = concept.trim().lowercase(Locale.ROOT)
        val explanation = when {
            clean.contains("quantum") -> "Quantum Physics: Explores subatomic matter where particles exist in superpositions and entangled states until measured."
            clean.contains("dijkstra") -> "Dijkstra's Algorithm: A greedy graph search algorithm that finds the shortest path between nodes by maintaining tentative distances."
            clean.contains("neural") || clean.contains("ai") -> "Neural Networks: Computational models inspired by biological neurons that learn mathematical representations from training data."
            clean.contains("recursion") -> "Recursion: A problem-solving method where a function calls itself to solve smaller sub-instances of the same problem until reaching a base case."
            else -> "Concept ($concept): An offline structured breakdown is available. VENUS suggests connecting online for comprehensive multi-perspective analogies."
        }
        return Result.success(explanation)
    }

    fun queryOfflineKnowledge(query: String): Result<String> {
        val clean = query.trim().lowercase(Locale.ROOT)
        for ((key, value) in FACTS) {
            if (clean.contains(key)) {
                return Result.success(value)
            }
        }
        return Result.success("Offline Knowledge: Information for '$query' is recorded in the local archive. Weather, system metrics, and offline calculations are active.")
    }
}
