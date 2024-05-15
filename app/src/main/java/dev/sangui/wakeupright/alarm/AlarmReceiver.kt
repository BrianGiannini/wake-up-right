package dev.sangui.wakeupright.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.sangui.wakeupright.R
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import org.koin.java.KoinJavaComponent.inject

class AlarmReceiver : BroadcastReceiver() {
    private val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "CANCEL_VIBRATION") {
            setupClockViewModel.stopVibration()
            return
        }

        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val channelId = "alarm_id"
        val notificationId = System.currentTimeMillis().toInt()  // Use current time as unique ID

        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Make phone vibrate
            setupClockViewModel.startVibration()

            // Create the NotificationChannel, but only on API 26+ because
            val name = "Wake up notification"
            val descriptionText = "The notification shown when the alarm goes off"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)

            // Intent to cancel vibration
            val cancelIntent = Intent(ctx, AlarmReceiver::class.java).apply {
                action = "CANCEL_VIBRATION"
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(ctx, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)  // Ensure the notification can be dismissed
                .addAction(R.drawable.ic_launcher_foreground, "Stop", cancelPendingIntent)  // Add action to stop vibration

            notificationManager.notify(notificationId, builder.build())
        }

    }

}