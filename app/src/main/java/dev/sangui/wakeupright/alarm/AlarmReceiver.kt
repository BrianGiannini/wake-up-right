package dev.sangui.wakeupright.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import dev.sangui.wakeupright.MainActivity
import dev.sangui.wakeupright.R
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AlarmReceiver : BroadcastReceiver() {

    private val ringToneProvider: RingToneProvider by inject(RingToneProvider::class.java)
    private val alarmConfig: AlarmConfig by inject(AlarmConfig::class.java)
    private val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Received alarm")

        if (intent?.action == "CANCEL_RINGTONE") {
            Log.d("AlarmReceiver", "cancel alarm")
            ringToneProvider.stopRingtone()

            val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
            if (notificationId != -1) {
                val notificationManager =
                    context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            }

            context?.let {
                setupClockViewModel.viewModelScope.launch {
                    setupClockViewModel.clearScheduledDate()
                }
            }

            return
        }

        context?.let {
            ringToneProvider.playRingtone(it)
        }

        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val channelId = "alarm_id"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", alarmConfig.notificationId)

        context.let { ctx ->
            val notificationManager =
                ctx?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "Wake up notification"
            val descriptionText = "The notification shown when the alarm goes off"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)

            // Intent to cancel ringtone
            val cancelIntent = Intent(ctx, AlarmReceiver::class.java).apply {
                action = "CANCEL_RINGTONE"
                putExtra("NOTIFICATION_ID", notificationId)
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(
                ctx,
                notificationId,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // intent to erase notification when swipe
            val deleteIntent = Intent(ctx, AlarmReceiver::class.java).apply {
                action = "CANCEL_RINGTONE"
                putExtra("NOTIFICATION_ID", notificationId)
            }
            val deletePendingIntent = PendingIntent.getBroadcast(
                ctx,
                notificationId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Intent to open the app
            val openAppIntent = Intent(ctx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                ctx,
                notificationId + 1,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openAppPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", cancelPendingIntent)

            notificationManager.notify(notificationId, builder.build())
        }

    }

}