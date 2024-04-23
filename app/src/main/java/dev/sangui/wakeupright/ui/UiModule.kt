package dev.sangui.wakeupright.ui

import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        SetupClockViewModel(get())
    }}