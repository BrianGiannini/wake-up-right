package dev.sangui.wakeupright.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.sangui.wakeupright.Constants
import dev.sangui.wakeupright.MainActivity
import org.koin.java.KoinJavaComponent.inject
import dev.sangui.wakeupright.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private val ringToneProvider: RingToneProvider by inject(RingToneProvider::class.java)
    private val alarmConfig: AlarmConfig by inject(AlarmConfig::class.java)
    private val alarmEventBus: AlarmEventBus by inject(AlarmEventBus::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        val ctx = context ?: return

        if (intent?.action == Constants.ACTION_CANCEL_RINGTONE) {
            ringToneProvider.stopRingtone()

            val notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, -1)
            if (notificationId != -1) {
                val notificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            }

            ctx.let {
                CoroutineScope(Dispatchers.IO).launch {
                    // Emit the alarm dismissed event
                    alarmEventBus.emitAlarmDismissed()
                }
            }

            return
        }

        ctx.let {
            ringToneProvider.playRingtone(it)
        }

        val message = intent?.getStringExtra(Constants.EXTRA_MESSAGE) ?: return
        val channelId = Constants.NOTIFICATION_CHANNEL_ID
        val notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, alarmConfig.notificationId)

        ctx.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Intent to cancel ringtone
            val cancelIntent = Intent(ctx, AlarmReceiver::class.java).apply {
                action = Constants.ACTION_CANCEL_RINGTONE
                putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId)
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(
                ctx,
                notificationId,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // intent to erase notification when swipe
            val deleteIntent = Intent(ctx, AlarmReceiver::class.java).apply {
                action = Constants.ACTION_CANCEL_RINGTONE
                putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId)
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
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(ctx.getString(R.string.notification_title))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(ctx.getString(R.string.notification_big_text)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ctx.getColor(R.color.ic_launcher_background))
                .setAutoCancel(true)
                .setContentIntent(openAppPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(R.drawable.ic_alarm, ctx.getString(R.string.notification_dismiss_action), cancelPendingIntent)

            notificationManager.notify(notificationId, builder.build())
        }

    }

}