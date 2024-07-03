package dev.sangui.wakeupright.ui.mainmenu

import org.koin.dsl.module

val uiModule = module {
    single {
        SetupClockViewModel(get(), get(), get())
    }
}