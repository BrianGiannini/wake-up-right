package dev.sangui.wakeupright.alarm

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(requestCode: Int)}