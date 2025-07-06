package dev.sangui.wakeupright.alarm

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmModule = module {
    single { AlarmConfig(notificationId = 12345) }
    single<AlarmScheduler> { AlarmSchedulerImpl(androidContext()) }
    single<DataStoreManager> { DataStoreManager(androidContext()) }
    single<AlarmReceiver> { AlarmReceiver() }
    single<RingToneProvider> { RingToneProvider() }
}