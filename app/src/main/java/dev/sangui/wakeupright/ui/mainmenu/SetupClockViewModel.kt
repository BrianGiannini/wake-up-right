package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dev.sangui.wakeupright.alarm.AlarmConfig
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmReceiver
import dev.sangui.wakeupright.alarm.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar


class SetupClockViewModel(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
    private val alarmConfig: AlarmConfig
) : AndroidViewModel(application) {


    private var alarmItem: AlarmItem? = null
    private val _scheduledDate = MutableStateFlow<Calendar?>(null)
    val scheduledDate: StateFlow<Calendar?> get() = _scheduledDate.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var notificationId: Int? = null

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    fun scheduleAlarm(hours: Int, minutes: Int) {
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the selected time is in the past, add a day
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmItem = AlarmItem(
            alarmTime = time,
            message = "Wake up!",
        )
        try {
            alarmItem?.let(alarmScheduler::schedule)
            _scheduledDate.value = time

            // Store the notification ID
            notificationId = alarmConfig.notificationId
        } catch (e: Exception) {
            _scheduledDate.value = null
            Log.e("SetupClockViewModel", "Error scheduling alarm", e)
        }
    }

    fun cancelAlarm(context: Context) {
        try {
            alarmItem?.let {
                alarmScheduler.cancel(it.hashCode())
            }
            _scheduledDate.value = null

            // Intent to cancel vibration
            notificationId?.let { id ->
                val cancelIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = "CANCEL_RINGTONE"
                    putExtra("NOTIFICATION_ID", id)
                }
                val cancelPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                cancelPendingIntent.send()
            }
        } catch (e: Exception) {
            Log.e("SetupClockViewModel", "Error cancelling alarm", e)
        }
    }

    fun clearScheduledDate() {
        _scheduledDate.value = null
    }

}