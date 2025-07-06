package dev.sangui.wakeupright.ui.mainmenu


import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sangui.wakeupright.alarm.AlarmConfig
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmReceiver
import dev.sangui.wakeupright.alarm.AlarmScheduler
import dev.sangui.wakeupright.alarm.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

import dev.sangui.wakeupright.Constants
import dev.sangui.wakeupright.R

class SetupClockViewModel(
    private val alarmScheduler: AlarmScheduler,
    private val alarmConfig: AlarmConfig,
    private val dataStoreManager: DataStoreManager,
    application: Application
) : AndroidViewModel(application) {

    private var alarmItem: AlarmItem? = null
    private val _scheduledDate = MutableStateFlow<Calendar?>(null)
    val scheduledDate: StateFlow<Calendar?> get() = _scheduledDate.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var notificationId: Int? = null

    init {
        viewModelScope.launch {
            val scheduledTime = dataStoreManager.scheduledAlarmTimeFlow().first()
            val storedNotificationId = dataStoreManager.notificationIdFlow().first()
            if (scheduledTime != null && storedNotificationId != null) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = scheduledTime
                }
                _scheduledDate.value = calendar
                notificationId = storedNotificationId
            }
        }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    fun scheduleAlarm(hours: Int, minutes: Int) {
        val time = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, hours)
            add(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        alarmItem = AlarmItem(
            alarmTime = time,
            message = getApplication<Application>().getString(R.string.alarm_message),
        )
        try {
            alarmItem?.let(alarmScheduler::schedule)
            _scheduledDate.value = time
            notificationId = alarmConfig.notificationId
            viewModelScope.launch {
                dataStoreManager.saveScheduledAlarmTime(time.timeInMillis)
                notificationId?.let { dataStoreManager.saveNotificationId(it) }
            }
        } catch (e: Exception) {
            _scheduledDate.value = null
            Log.e("SetupClockViewModel", getApplication<Application>().getString(R.string.error_scheduling_alarm), e)
        }
    }

    fun cancelAlarm(context: Context) {
        try {
            val time = _scheduledDate.value
            if (time != null) {
                alarmItem = AlarmItem(
                    alarmTime = time,
                    message = getApplication<Application>().getString(R.string.alarm_message),
                )
                alarmItem?.let { alarmScheduler.cancel(it.hashCode()) }
            }

            _scheduledDate.value = null
            viewModelScope.launch {
                dataStoreManager.clearScheduledAlarmTime()
                dataStoreManager.clearNotificationId()
            }

            notificationId?.let { id ->
                val cancelIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = Constants.ACTION_CANCEL_RINGTONE
                    putExtra(Constants.EXTRA_NOTIFICATION_ID, id)
                }
                val cancelPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                cancelPendingIntent.send()
            }
        } catch (e: Exception) {
            Log.e("SetupClockViewModel", getApplication<Application>().getString(R.string.error_cancelling_alarm), e)
        }
    }

    fun clearScheduledDate() {
        _scheduledDate.value = null
        viewModelScope.launch {
            dataStoreManager.clearScheduledAlarmTime()
            dataStoreManager.clearNotificationId()
        }
    }
}
