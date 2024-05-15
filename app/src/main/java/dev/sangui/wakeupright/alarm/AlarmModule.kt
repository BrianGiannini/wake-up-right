package dev.sangui.wakeupright.alarm

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmModule = module {
    single<AlarmScheduler> { AlarmSchedulerImpl(androidContext()) }
    single<DataStoreManager> { DataStoreManager(androidContext()) }
    single<AlarmReceiver> { AlarmReceiver() }
}