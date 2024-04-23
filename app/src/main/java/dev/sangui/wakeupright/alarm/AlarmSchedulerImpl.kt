package dev.sangui.wakeupright.alarm

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    override fun schedule(alarmItem: AlarmItem) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                runSchedule(alarmItem)
            } else {
                showAlarmPermissionDialog()
            }
        } else {
            runSchedule(alarmItem)
        }

    }

    override fun cancel(alarmItem: AlarmItem) {
        PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun runSchedule(alarmItem: AlarmItem) {
        /* val intent = Intent(context, AlarmReceiver::class.java).apply {
             putExtra("EXTRA_MESSAGE", alarmItem.message)
         }

         val alarmTime =
             alarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L
         alarmManager.setExactAndAllowWhileIdle(
             AlarmManager.RTC_WAKEUP,
             alarmTime,
             PendingIntent.getBroadcast(
                 context,
                 alarmItem.hashCode(),
                 intent,
                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
             )
         )
         Log.e("Alarm", "Alarm set at $alarmTime") */
    }

    private fun showAlarmPermissionDialog() {
        AlertDialog.Builder(context)
            .setTitle("Permission Required")
            .setMessage("This app requires permission to schedule exact alarms. Please enable it in the settings.")
            .setPositiveButton("Go to Settings") { dialog, which ->
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }

            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}