package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmScheduler
import dev.sangui.wakeupright.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class SetupClockViewModel(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
) : AndroidViewModel(application) {
    private val _permissionRequest = MutableStateFlow<Event<String>?>(null)
    val permissionRequest: StateFlow<Event<String>?> = _permissionRequest.asStateFlow()

    var alarmItem: AlarmItem? = null

    fun scheduleAlarm() {
        alarmItem = AlarmItem(
            alarmTime = LocalDateTime.now().plusSeconds(
                2.toLong()
            ),
            message = "messageText"
        )
        alarmItem?.let(alarmScheduler::schedule)

    }


}