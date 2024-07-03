package dev.sangui.wakeupright.alarm

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmModule = module {
    single { AlarmConfig(notificationId = 12345) } // Use your actual constant value here
    single<AlarmScheduler> { AlarmSchedulerImpl(androidContext()) }
    single<DataStoreManager> { DataStoreManager(androidContext()) }
    single<AlarmReceiver> { AlarmReceiver() }
    single<RingToneProvider> { RingToneProvider() }
}