package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmScheduler
import java.time.LocalDateTime

class SetupClockViewModel(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
) : AndroidViewModel(application) {

    private var alarmItem: AlarmItem? = null
    private var _scheduledDate: LocalDateTime? = null

    fun scheduleAlarm(hours: Int, minutes: Int) {
        val time = LocalDateTime.now().plusHours(hours.toLong()).plusMinutes(minutes.toLong())
        alarmItem = AlarmItem(
            alarmTime = time,
            message = "Wake up!"
        )
        alarmItem?.let(alarmScheduler::schedule)
        _scheduledDate = time
    }

    fun cancelAlarm() {
        alarmItem?.let {
            alarmScheduler.cancel(it.hashCode())
        }
        _scheduledDate = null
    }

    fun getScheduledDate(): LocalDateTime? {
        return _scheduledDate
    }

}