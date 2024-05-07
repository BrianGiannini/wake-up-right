package dev.sangui.wakeupright.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import dev.sangui.wakeupright.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val channelId = "alarm_id"
        val notificationId = System.currentTimeMillis().toInt()  // Use current time as unique ID

        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Make phone vibrate
            vibrate(context)

            // Create the NotificationChannel, but only on API 26+ because
            val name = "Wake up notification"
            val descriptionText = "The notification shown when the alarm goes off"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setBypassDnd(true)
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

    private fun vibrate(context: Context) {
        // Vibration code starts here
        val vibrator =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (vibrator?.hasVibrator() == true) {  // Check if the hardware has a vibrator
            val pattern = longArrayOf(200, 700, 600)
            val amplitude = intArrayOf(0, 255,0 )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // New API
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitude, 1))
            } else {
                // Deprecated in API 26
                vibrator.vibrate(pattern, 0)
            }
        }
    }
}