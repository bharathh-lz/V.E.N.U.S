package com.example.service

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.audio.VenusSpeechManager
import com.example.audio.VenusVoiceBiometrics

class VenusWakeLockService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var speechManager: VenusSpeechManager? = null

    companion object {
        const val ACTION_START_LISTENING = "com.example.service.START_LISTENING"
        const val ACTION_STOP_LISTENING = "com.example.service.STOP_LISTENING"
        const val CHANNEL_ID = "venus_lockscreen_wake_channel"
        const val NOTIFICATION_ID = 2002

        fun start(context: Context) {
            val intent = Intent(context, VenusWakeLockService::class.java).apply {
                action = ACTION_START_LISTENING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, VenusWakeLockService::class.java).apply {
                action = ACTION_STOP_LISTENING
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        acquireCpuWakeLock()
        startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_LISTENING) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundNotification()
        return START_STICKY
    }

    private fun acquireCpuWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "venus:always_on_cpu_wakelock"
            )?.apply {
                setReferenceCounted(false)
                acquire(12 * 60 * 60 * 1000L) // 12 hour max safety lease
            }
        } catch (_: Exception) {}
    }

    private fun startForegroundNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "V.E.N.U.S Always-On Listening",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Enables wake word detection and response even when device is locked"
            }
            notificationManager?.createNotificationChannel(channel)
        }

        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("SUMMON_VENUS", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val profile = VenusVoiceBiometrics.getVoiceProfile(this)
        val userName = profile.userName

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("V.E.N.U.S Listening Active")
            .setContentText("Say \"VENUS\" to wake device • Speaker: $userName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Wakes the device screen and brings MainActivity over lock screen
     */
    fun wakeScreenAndLaunchApp(command: String? = null) {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            @Suppress("DEPRECATION")
            val screenWakeLock = powerManager?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                PowerManager.ACQUIRE_CAUSES_WAKEUP or
                PowerManager.ON_AFTER_RELEASE,
                "venus:lock_screen_turn_on"
            )
            screenWakeLock?.acquire(8000L)

            val activityIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("SUMMON_VENUS", true)
                putExtra("LOCKSCREEN_WAKE", true)
                if (!command.isNullOrBlank()) {
                    putExtra("ATTACHED_COMMAND", command)
                }
            }
            startActivity(activityIntent)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            speechManager?.destroy()
        } catch (_: Exception) {}
    }
}
