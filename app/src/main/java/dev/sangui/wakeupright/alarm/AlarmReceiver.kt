package dev.sangui.wakeupright.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.sangui.wakeupright.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Received alarm broadcast")

        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val channelId = "alarm_id"
        val notificationId = System.currentTimeMillis().toInt()  // Use current time as unique ID

        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create the NotificationChannel, but only on API 26+ because
            val name = "Alarm Channel"
            val descriptionText = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Demo")
                .setContentText("Notification sent with message: $message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(notificationId, builder.build())
        }
    }
}