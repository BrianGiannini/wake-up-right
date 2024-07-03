package dev.sangui.wakeupright.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private fun LocalDateTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return this.atZone(zoneId).toInstant().toEpochMilli()
    }

    override fun schedule(alarmItem: AlarmItem) {
        Log.d("AlarmScheduler", "Scheduling alarm for ${alarmItem.alarmTime}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("EXTRA_MESSAGE", alarmItem.message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
//            convertTime(alarmItem.alarmTime),
            LocalDateTime.now().plusSeconds(5).toMillis(),
            pendingIntent
        )
    }

    private fun convertTime(alarmItem: LocalDateTime): Long {
        val zonedDateTime = alarmItem.atZone(ZoneId.systemDefault())
        val alarmTimeInMillis = zonedDateTime.toInstant().toEpochMilli()
        return alarmTimeInMillis
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