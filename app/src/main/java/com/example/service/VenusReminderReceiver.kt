package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.VenusDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VenusReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val reminderId = intent?.getLongExtra("REMINDER_ID", -1L) ?: -1L
        val title = intent?.getStringExtra("REMINDER_TITLE") ?: "V.E.N.U.S Reminder"

        showNotification(context, reminderId, title)

        // Mark completed in database
        if (reminderId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = VenusDatabase.getDatabase(context)
                val all = db.reminderDao().getDueReminders(System.currentTimeMillis() + 60000)
                all.find { it.id == reminderId }?.let {
                    db.reminderDao().updateReminder(it.copy(isCompleted = true))
                }
            }
        }
    }

    private fun showNotification(context: Context, reminderId: Long, title: String) {
        val channelId = "venus_reminders_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "V.E.N.U.S Reminders & Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when scheduled Venus voice reminders are due"
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("V.E.N.U.S AI Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager?.notify(reminderId.toInt().coerceAtLeast(1001), notification)
    }
}
