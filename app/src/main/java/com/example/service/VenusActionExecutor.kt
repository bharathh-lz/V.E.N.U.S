package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast
import com.example.audio.VenusEngineSoundGenerator
import com.example.data.local.ReminderEntity
import com.example.data.local.VenusDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

data class ActionResult(
    val success: Boolean,
    val message: String,
    val details: String? = null
)

class VenusActionExecutor(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var isTorchOn = false

    /**
     * Parses payload parameters from key="value" or key='value' or comma-separated tokens.
     */
    fun parsePayloadParameters(payload: String?): Map<String, String> {
        if (payload.isNullOrBlank()) return emptyMap()
        val params = mutableMapOf<String, String>()
        val regex = Regex("""([a-zA-Z_]+)\s*=\s*(?:"([^"]*)"|'([^']*)'|([^,]+))""")
        val matches = regex.findAll(payload)
        for (m in matches) {
            val key = m.groupValues[1].trim().lowercase(Locale.ROOT)
            val value = (m.groupValues[2].ifEmpty { m.groupValues[3] }.ifEmpty { m.groupValues[4] }).trim()
            params[key] = value
        }
        if (params.isEmpty()) {
            params["raw"] = payload.trim()
        }
        return params
    }

    /**
     * Executes parsed action request from voice, multi-tasking queue, or AI reasoning.
     */
    fun execute(actionType: String, payload: String? = null): ActionResult {
        return try {
            val params = parsePayloadParameters(payload)
            val raw = params["raw"] ?: payload ?: ""

            val result = when (actionType.uppercase(Locale.ROOT)) {
                // Cross-App Friend Chat & Messaging Actions
                "SEND_WHATSAPP", "WHATSAPP_MESSAGE" -> {
                    val to = params["to"] ?: params["recipient"] ?: params["target"] ?: ""
                    val msg = params["message"] ?: params["msg"] ?: params["text"] ?: raw
                    sendWhatsAppMessage(to, msg)
                }
                "SEND_TELEGRAM", "TELEGRAM_MESSAGE" -> {
                    val to = params["to"] ?: params["recipient"] ?: ""
                    val msg = params["message"] ?: params["msg"] ?: params["text"] ?: raw
                    sendTelegramMessage(to, msg)
                }
                "SEND_SMS", "SMS_MESSAGE" -> {
                    val to = params["to"] ?: params["recipient"] ?: ""
                    val msg = params["message"] ?: params["msg"] ?: params["text"] ?: raw
                    sendSmsMessage(to, msg)
                }
                "SEND_INSTAGRAM", "INSTAGRAM_MESSAGE" -> {
                    val to = params["to"] ?: params["recipient"] ?: ""
                    val msg = params["message"] ?: params["msg"] ?: params["text"] ?: raw
                    sendInstagramMessage(to, msg)
                }
                "CHAT_FRIEND", "SEND_MESSAGE", "OPEN_CHAT_APP" -> {
                    val app = params["app"] ?: params["platform"]
                    val to = params["to"] ?: params["recipient"] ?: ""
                    val msg = params["message"] ?: params["msg"] ?: params["text"] ?: raw
                    chatWithFriend(app, to, msg)
                }

                // Phone Calls & Contacts
                "CALL_CONTACT", "MAKE_CALL" -> callContact(raw)

                // App Launching & System Navigation
                "OPEN_APP" -> {
                    val target = params["target"] ?: params["app"] ?: raw
                    openApp(target)
                }
                "CLOSE_APP", "GO_HOME", "EXIT_APP" -> goHome()
                "OPEN_WEBSITE" -> {
                    val url = params["url"] ?: params["target"] ?: raw
                    openWebsite(url)
                }
                "SEARCH_WEB", "SEARCH_GOOGLE" -> {
                    val query = params["query"] ?: params["target"] ?: raw
                    searchGoogle(query)
                }
                "SEARCH_YOUTUBE" -> {
                    val query = params["query"] ?: params["target"] ?: raw
                    searchYouTube(query)
                }
                "SEND_EMAIL", "COMPOSE_EMAIL" -> composeEmail(raw)
                "MANAGE_FILES", "OPEN_FILES" -> openFileManager()
                "TAKE_SCREENSHOT" -> takeScreenshot()
                "PLAY_MUSIC" -> {
                    val query = params["query"] ?: raw
                    playMusic(query)
                }

                // Hardware Controls
                "FLASHLIGHT_ON" -> setFlashlight(true)
                "FLASHLIGHT_OFF" -> setFlashlight(false)
                "FLASHLIGHT_TOGGLE" -> toggleFlashlight()
                "VOLUME_UP" -> adjustVolume(AudioManager.ADJUST_RAISE)
                "VOLUME_DOWN" -> adjustVolume(AudioManager.ADJUST_LOWER)
                "VOLUME_MUTE" -> adjustVolume(AudioManager.ADJUST_MUTE)
                "CHECK_BATTERY" -> checkBattery()

                // System Settings
                "SETTINGS_WIFI" -> openSettings(Settings.ACTION_WIFI_SETTINGS, "Wi-Fi Settings")
                "SETTINGS_BLUETOOTH" -> openSettings(Settings.ACTION_BLUETOOTH_SETTINGS, "Bluetooth Settings")
                "SETTINGS_DISPLAY" -> openSettings(Settings.ACTION_DISPLAY_SETTINGS, "Display Settings")
                "SETTINGS_SOUND" -> openSettings(Settings.ACTION_SOUND_SETTINGS, "Sound Settings")
                "SETTINGS_MAIN" -> openSettings(Settings.ACTION_SETTINGS, "System Settings")
                "SET_REMINDER" -> {
                    val title = params["title"] ?: raw.ifBlank { "Task Reminder" }
                    scheduleReminder(title)
                }
                else -> ActionResult(false, "Unknown action directive: $actionType")
            }

            if (result.success) {
                // Play affirmative high-tech acoustic chirp
                VenusEngineSoundGenerator.playActionSound()
            }

            result
        } catch (e: Exception) {
            ActionResult(false, "Action failed: ${e.localizedMessage}")
        }
    }

    /**
     * Cross-App Friend Chat: Dispatches message through specific app or opens universal chooser
     * to chat with friends across WhatsApp, Telegram, Instagram, SMS, Signal, Discord, etc.
     */
    fun chatWithFriend(app: String?, recipient: String, message: String): ActionResult {
        val cleanApp = app?.trim()?.lowercase(Locale.ROOT)
        return when {
            cleanApp == "whatsapp" -> sendWhatsAppMessage(recipient, message)
            cleanApp == "telegram" -> sendTelegramMessage(recipient, message)
            cleanApp == "sms" || cleanApp == "text" -> sendSmsMessage(recipient, message)
            cleanApp == "instagram" || cleanApp == "ig" -> sendInstagramMessage(recipient, message)
            else -> {
                // Omni-Messenger Chooser: Shows all messaging apps installed on device!
                val cleanMsg = if (message.isNotBlank()) message else "Hello from V.E.N.U.S!"
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, cleanMsg)
                    if (recipient.isNotBlank()) {
                        putExtra(Intent.EXTRA_SUBJECT, "Message for $recipient")
                    }
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val title = if (recipient.isNotBlank()) "Chat with $recipient via:" else "Chat with friends via:"
                val chooser = Intent.createChooser(sendIntent, title).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                try {
                    context.startActivity(chooser)
                    ActionResult(
                        true,
                        "Opened Chat Dispatcher for ${if (recipient.isNotBlank()) recipient else "friends"} across all messaging apps.",
                        cleanMsg
                    )
                } catch (e: Exception) {
                    ActionResult(false, "Could not launch chat dispatcher: ${e.message}")
                }
            }
        }
    }

    /**
     * Sends message or opens chat with a friend on WhatsApp.
     */
    fun sendWhatsAppMessage(recipient: String, message: String): ActionResult {
        val cleanMsg = message.trim().ifEmpty { "Hello!" }
        val cleanRecipient = recipient.trim()

        var phoneNumber: String? = null
        if (cleanRecipient.isNotBlank()) {
            val isDigits = cleanRecipient.replace("+", "").replace("-", "").replace(" ", "").all { it.isDigit() }
            if (isDigits && cleanRecipient.length >= 7) {
                phoneNumber = cleanRecipient.replace("+", "").replace("-", "").replace(" ", "")
            } else {
                val contact = com.example.audio.VenusContactManager.findContact(context, cleanRecipient)
                if (contact != null) {
                    phoneNumber = contact.phoneNumber.replace("+", "").replace("-", "").replace(" ", "")
                }
            }
        }

        return try {
            if (phoneNumber != null && phoneNumber.isNotBlank()) {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(cleanMsg)}")
                val waIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(waIntent)
                ActionResult(true, "Opened WhatsApp chat with ${if (cleanRecipient.isNotBlank()) cleanRecipient else phoneNumber} with message ready.", cleanMsg)
            } else {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage("com.whatsapp")
                    putExtra(Intent.EXTRA_TEXT, cleanMsg)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(shareIntent)
                ActionResult(true, "Opened WhatsApp with message ready to send${if (cleanRecipient.isNotBlank()) " to $cleanRecipient" else ""}.", cleanMsg)
            }
        } catch (e: Exception) {
            // Fallback to WhatsApp Web or generic chat intent
            try {
                val webUri = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(cleanMsg)}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                ActionResult(true, "Opened WhatsApp link with message ready.", cleanMsg)
            } catch (_: Exception) {
                ActionResult(false, "WhatsApp is not installed on this device.")
            }
        }
    }

    /**
     * Sends message or opens chat with a friend on Telegram.
     */
    fun sendTelegramMessage(recipient: String, message: String): ActionResult {
        val cleanMsg = message.trim().ifEmpty { "Hello from V.E.N.U.S!" }
        val cleanRecipient = recipient.trim()

        return try {
            val tgUri = Uri.parse("tg://msg?text=${Uri.encode(cleanMsg)}")
            val tgIntent = Intent(Intent.ACTION_VIEW, tgUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(tgIntent)
            ActionResult(true, "Opened Telegram chat${if (cleanRecipient.isNotBlank()) " for $cleanRecipient" else ""} with message prepared.", cleanMsg)
        } catch (e: Exception) {
            try {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage("org.telegram.messenger")
                    putExtra(Intent.EXTRA_TEXT, cleanMsg)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(sendIntent)
                ActionResult(true, "Opened Telegram with message ready.", cleanMsg)
            } catch (_: Exception) {
                ActionResult(false, "Telegram is not installed on this device.")
            }
        }
    }

    /**
     * Sends SMS message to a friend.
     */
    fun sendSmsMessage(recipient: String, message: String): ActionResult {
        val cleanMsg = message.trim()
        val cleanRecipient = recipient.trim()

        var phoneNumber = cleanRecipient
        if (cleanRecipient.isNotBlank()) {
            val isDigits = cleanRecipient.replace("+", "").replace("-", "").replace(" ", "").all { it.isDigit() }
            if (!isDigits) {
                val contact = com.example.audio.VenusContactManager.findContact(context, cleanRecipient)
                if (contact != null) {
                    phoneNumber = contact.phoneNumber
                }
            }
        }

        return try {
            val smsUri = if (phoneNumber.isNotBlank()) Uri.parse("smsto:$phoneNumber") else Uri.parse("smsto:")
            val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                putExtra("sms_body", cleanMsg)
                putExtra(Intent.EXTRA_TEXT, cleanMsg)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(smsIntent)
            ActionResult(true, "SMS draft ready for ${if (cleanRecipient.isNotBlank()) cleanRecipient else "recipient"}.", cleanMsg)
        } catch (e: Exception) {
            ActionResult(false, "Could not open SMS application: ${e.message}")
        }
    }

    /**
     * Opens Instagram Direct chat for friends.
     */
    fun sendInstagramMessage(recipient: String, message: String): ActionResult {
        val cleanMsg = message.trim().ifEmpty { "Hello!" }
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.instagram.android")
                putExtra(Intent.EXTRA_TEXT, cleanMsg)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ActionResult(true, "Opened Instagram Direct with message ready for friends.", cleanMsg)
        } catch (e: Exception) {
            try {
                val directUri = Uri.parse("https://instagram.com/direct/inbox/")
                val webIntent = Intent(Intent.ACTION_VIEW, directUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                ActionResult(true, "Opened Instagram Direct inbox.", cleanMsg)
            } catch (_: Exception) {
                ActionResult(false, "Instagram is not installed on this device.")
            }
        }
    }

    private fun goHome(): ActionResult {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(homeIntent)
        return ActionResult(true, "Navigated to Home screen")
    }

    private fun openWebsite(url: String): ActionResult {
        val formattedUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.contains(".") -> "https://$url"
            else -> "https://www.google.com/search?q=${Uri.encode(url)}"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return ActionResult(true, "Opened website: $formattedUrl")
    }

    private fun composeEmail(payload: String): ActionResult {
        var recipient = ""
        var subject = "Assistance from V.E.N.U.S"
        var body = payload

        if (payload.contains("to=") || payload.contains("subject=") || payload.contains("body=")) {
            val parts = payload.split(",")
            for (p in parts) {
                val kv = p.trim().split("=", limit = 2)
                if (kv.size == 2) {
                    when (kv[0].trim().lowercase(Locale.ROOT)) {
                        "to", "recipient" -> recipient = kv[1].trim()
                        "subject" -> subject = kv[1].trim()
                        "body", "text", "msg" -> body = kv[1].trim()
                    }
                }
            }
        }

        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(mailIntent)
            ActionResult(true, "Email client opened with draft for ${if (recipient.isNotBlank()) recipient else "recipient"}")
        } catch (e: Exception) {
            ActionResult(false, "No email client found on device")
        }
    }

    private fun openFileManager(): ActionResult {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            ActionResult(true, "Opened File Manager storage explorer")
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallback)
                ActionResult(true, "Opened Storage settings")
            } catch (_: Exception) {
                ActionResult(false, "Could not open file manager")
            }
        }
    }

    private fun takeScreenshot(): ActionResult {
        val message = "Screenshot trigger activated. Press Power + Volume Down or use the Quick Settings Tile."
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        return ActionResult(true, message)
    }

    private fun playMusic(query: String): ActionResult {
        val clean = query.trim()
        val musicIntent = if (clean.isNotBlank()) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/${Uri.encode(clean)}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MUSIC)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        return try {
            context.startActivity(musicIntent)
            ActionResult(true, if (clean.isNotBlank()) "Playing \"$clean\"" else "Opened Music Player")
        } catch (e: Exception) {
            val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(if (clean.isNotBlank()) clean else "synthwave music")}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(ytIntent)
            ActionResult(true, "Launched YouTube Music stream")
        }
    }

    private fun openApp(appNameOrPackage: String): ActionResult {
        val target = appNameOrPackage.trim().lowercase(Locale.ROOT)
        val pm = context.packageManager

        // Common known intents for standard Android components
        val commonIntent: Intent? = when {
            target.contains("camera") || target.contains("கேமரா") -> Intent("android.media.action.IMAGE_CAPTURE")
            target.contains("calculator") || target.contains("கால்குலேட்டர்") -> getCalculatorIntent()
            target.contains("gallery") || target.contains("photos") || target.contains("புகைப்படம்") -> Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"
            }
            target.contains("clock") || target.contains("alarm") || target.contains("timer") -> Intent(AlarmClock.ACTION_SHOW_ALARMS)
            target.contains("setting") || target.contains("அமைப்புகள்") -> Intent(Settings.ACTION_SETTINGS)
            target.contains("browser") || target.contains("chrome") -> Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
            target.contains("youtube") || target.contains("யூடியூப்") -> Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))
            target.contains("whatsapp") || target.contains("வாட்ஸ்அப்") -> pm.getLaunchIntentForPackage("com.whatsapp")
            target.contains("telegram") || target.contains("டெலிகிராம்") -> pm.getLaunchIntentForPackage("org.telegram.messenger")
            target.contains("instagram") || target.contains("இன்ஸ்டாகிராம்") -> pm.getLaunchIntentForPackage("com.instagram.android")
            target.contains("map") -> Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=nearby"))
            target.contains("message") || target.contains("sms") || target.contains("செய்தி") -> Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            target.contains("dialer") || target.contains("phone") || target.contains("call") -> Intent(Intent.ACTION_DIAL)
            else -> null
        }

        if (commonIntent != null) {
            commonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(commonIntent)
                return ActionResult(true, "Launched $appNameOrPackage")
            } catch (_: Exception) {}
        }

        // Search installed application list
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var matchIntent: Intent? = null
        var matchedLabel: String? = null

        for (app in installedApps) {
            val label = pm.getApplicationLabel(app).toString().lowercase(Locale.ROOT)
            val pkg = app.packageName.lowercase(Locale.ROOT)

            if (label.contains(target) || pkg.contains(target) || target.contains(label)) {
                val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    matchIntent = launchIntent
                    matchedLabel = pm.getApplicationLabel(app).toString()
                    break
                }
            }
        }

        if (matchIntent != null) {
            matchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(matchIntent)
            return ActionResult(true, "Launched application: $matchedLabel")
        }

        // Fallback: search Play Store or Web
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(target)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
        return ActionResult(true, "App not locally matched. Opened web lookup for \"$appNameOrPackage\"")
    }

    private fun getCalculatorIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_CALCULATOR)
        return intent
    }

    private fun searchGoogle(query: String): ActionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ActionResult(true, "Opened Google search for \"$query\"")
    }

    private fun searchYouTube(query: String): ActionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ActionResult(true, "Opened YouTube search for \"$query\"")
    }

    private fun setFlashlight(enable: Boolean): ActionResult {
        return try {
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null && cameraManager != null) {
                cameraManager.setTorchMode(cameraId, enable)
                isTorchOn = enable
                ActionResult(true, if (enable) "Flashlight activated" else "Flashlight deactivated")
            } else {
                ActionResult(false, "Flashlight hardware unavailable")
            }
        } catch (e: Exception) {
            ActionResult(false, "Flashlight control error: ${e.message}")
        }
    }

    private fun toggleFlashlight(): ActionResult {
        return setFlashlight(!isTorchOn)
    }

    private fun adjustVolume(direction: Int): ActionResult {
        return try {
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                direction,
                AudioManager.FLAG_SHOW_UI
            )
            val currentVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
            val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 100
            val pct = (currentVol * 100) / maxVol
            ActionResult(true, "System volume updated to $pct%", "Level: $currentVol / $maxVol")
        } catch (e: Exception) {
            ActionResult(false, "Volume adjustment failed: ${e.message}")
        }
    }

    private fun checkBattery(): ActionResult {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val level = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
        val isCharging = bm?.isCharging ?: false
        val statusText = if (level >= 0) {
            "Battery level is at $level%${if (isCharging) " (Charging)" else " (Discharging)"}."
        } else {
            "Battery telemetry is currently unavailable."
        }
        return ActionResult(true, statusText, "Level: $level%, Charging: $isCharging")
    }

    private fun openSettings(action: String, name: String): ActionResult {
        return try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Opened $name")
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
            ActionResult(true, "Opened System Settings")
        }
    }

    private fun scheduleReminder(reminderTitle: String): ActionResult {
        val triggerTime = System.currentTimeMillis() + (10 * 60 * 1000)
        CoroutineScope(Dispatchers.IO).launch {
            val db = VenusDatabase.getDatabase(context)
            val id = db.reminderDao().insertReminder(
                ReminderEntity(
                    title = reminderTitle,
                    targetTimeMillis = triggerTime
                )
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, VenusReminderReceiver::class.java).apply {
                putExtra("REMINDER_ID", id)
                putExtra("REMINDER_TITLE", reminderTitle)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } catch (_: Exception) {}
        }

        return ActionResult(true, "Reminder scheduled: \"$reminderTitle\"")
    }

    private fun callContact(queryOrNumber: String): ActionResult {
        val clean = queryOrNumber.trim()
        if (clean.isBlank()) {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(dialIntent)
            return ActionResult(true, "Opened Phone Dialer")
        }

        val isNumeric = clean.replace("+", "").replace("-", "").replace(" ", "").all { it.isDigit() }
        if (isNumeric && clean.length >= 3) {
            val success = com.example.audio.VenusContactManager.makePhoneCall(context, clean)
            return if (success) {
                ActionResult(true, "Calling $clean", clean)
            } else {
                ActionResult(false, "Could not initiate call to $clean")
            }
        }

        val contact = com.example.audio.VenusContactManager.findContact(context, clean)
        return if (contact != null) {
            val success = com.example.audio.VenusContactManager.makePhoneCall(context, contact.phoneNumber)
            if (success) {
                ActionResult(true, "Calling ${contact.name} (${contact.phoneNumber})", contact.phoneNumber)
            } else {
                ActionResult(false, "Could not dial ${contact.name}")
            }
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(dialIntent)
                ActionResult(true, "Contact \"$clean\" not found in phonebook. Opened dialer.", clean)
            } catch (e: Exception) {
                ActionResult(false, "Contact \"$clean\" not found.")
            }
        }
    }
}
