package dev.sangui.wakeupright

import android.app.Application
import dev.sangui.wakeupright.ui.mainmenu.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppApplication)

            modules(
                uiModule,
            )
        }
    }
}