package dev.sangui.wakeupright.ui.mainmenu

import androidx.lifecycle.ViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmScheduler

class SetupClockViewModel(private val alarmScheduler: AlarmScheduler): ViewModel() {

    fun setupAlarm(alarmItem: AlarmItem) {
        alarmScheduler.schedule(alarmItem)
    }

}