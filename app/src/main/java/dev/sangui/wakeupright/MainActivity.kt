package dev.sangui.wakeupright

import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dev.sangui.wakeupright.alarm.DataStoreManager
import dev.sangui.wakeupright.ui.mainmenu.SetupClockScreen
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import dev.sangui.wakeupright.ui.theme.WakeUpRightTheme
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    private val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)
    private val dataStoreManager: DataStoreManager by inject(DataStoreManager::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val ringtonePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                }
                uri?.let {
                    // Save the selected ringtone URI in DataStore
                    lifecycleScope.launch {
                        dataStoreManager.saveSelectedRingtone(it.toString())
                    }
                }
            }
        }

        setContent {
            WakeUpRightTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupClockScreen(setupClockViewModel, dataStoreManager, ringtonePickerLauncher)
                }
            }
        }

    }

}