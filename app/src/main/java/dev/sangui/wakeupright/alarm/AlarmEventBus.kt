package dev.sangui.wakeupright.alarm

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AlarmEventBus {
    private val _alarmDismissed = MutableSharedFlow<Unit>()
    val alarmDismissed: SharedFlow<Unit> = _alarmDismissed.asSharedFlow()

    suspend fun emitAlarmDismissed() {
        _alarmDismissed.emit(Unit)
    }
}