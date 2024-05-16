package dev.sangui.wakeupright

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.sangui.wakeupright.alarm.DataStoreManager
import dev.sangui.wakeupright.ui.mainmenu.SetupClockScreen
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import dev.sangui.wakeupright.ui.theme.WakeUpRightTheme
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    private val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)
    private val dataStoreManager: DataStoreManager by inject(DataStoreManager::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WakeUpRightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupClockScreen(setupClockViewModel, dataStoreManager)
                }
            }
        }

    }

}