package dev.sangui.wakeupright.alarm

import android.content.Context

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(requestCode: Int)}