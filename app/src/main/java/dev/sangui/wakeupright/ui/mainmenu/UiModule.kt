package dev.sangui.wakeupright.ui.mainmenu

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel<SetupClockViewModel> {
        SetupClockViewModel(
            alarmScheduler = get(),
            alarmConfig = get(),
            dataStoreManager = get(),
            alarmEventBus = get(),
            application = androidApplication(),
        )
    }
}