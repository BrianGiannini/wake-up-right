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

    fun scheduleAlarm() {
        val time = LocalDateTime.now().plusSeconds(2.toLong())
        Log.d(
            "Alarm",
            "alarm" + "year" + time.year + "dayOfYear" + time.dayOfYear + "month" + time.month + "hour" + time.hour + "minute" + time.minute + "second" + time.second
        )
        alarmItem = AlarmItem(
            alarmTime = LocalDateTime.now().plusSeconds(10.toLong()),
            message = "messageText"
        )
        alarmItem?.let(alarmScheduler::schedule)
    }

    fun cancelAlarm() {
        alarmItem?.let {
            alarmScheduler.cancel(it.hashCode())
        }
    }


}