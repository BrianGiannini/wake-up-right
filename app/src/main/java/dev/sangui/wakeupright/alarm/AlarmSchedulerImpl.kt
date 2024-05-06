package dev.sangui.wakeupright.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun LocalDateTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return this.atZone(zoneId).toInstant().toEpochMilli()
    }

    override fun schedule(alarmItem: AlarmItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java, )
        intent.putExtra("EXTRA_MESSAGE", "wake up Neo")
        val pendingIntent = PendingIntent.getBroadcast(context,  alarmItem.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmTimeInMillis = LocalDateTime.now().plusSeconds(5).toMillis()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent)
    }

    override fun cancel(alarmItem: AlarmItem) {
        val alarmClockInfo = alarmManager.nextAlarmClock
        if (alarmClockInfo != null) {
            // Alarm is scheduled
            val scheduledTime = alarmClockInfo.triggerTime
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(scheduledTime)
            Log.d("alarmClockInfo", "BEFORE alarmClockInfo formattedTime : $formattedTime" )
        } else {
            Log.d("alarmClockInfo", "BEFORE no alarm clock" )
            // No alarm is scheduled
        }

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )


        if (alarmClockInfo != null) {
            // Alarm is scheduled
            val scheduledTime = alarmClockInfo.triggerTime
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(scheduledTime)
            Log.d("alarmClockInfo", "AFTER alarmClockInfo formattedTime : $formattedTime" )
        } else {
            Log.d("alarmClockInfo", "AFTER no alarm clock" )
            // No alarm is scheduled
        }

    }

}