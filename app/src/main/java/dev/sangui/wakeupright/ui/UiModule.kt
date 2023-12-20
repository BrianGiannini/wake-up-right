package dev.sangui.wakeupright.ui

import dev.sangui.wakeupright.alarm.AlarmScheduler
import dev.sangui.wakeupright.alarm.AlarmSchedulerImpl
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        SetupClockViewModel(get())
    }}