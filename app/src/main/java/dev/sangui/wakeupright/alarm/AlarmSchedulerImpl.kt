package dev.sangui.wakeupright.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import dev.sangui.wakeupright.Constants

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    override fun schedule(alarmItem: AlarmItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.EXTRA_MESSAGE, alarmItem.message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.alarmTime.timeInMillis,
            pendingIntent,
        )
    }

    override fun cancel(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

}