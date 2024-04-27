package dev.sangui.wakeupright

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.sangui.wakeupright.ui.mainmenu.SetupClockScreen
import dev.sangui.wakeupright.ui.mainmenu.SetupClockViewModel
import dev.sangui.wakeupright.ui.theme.WakeUpRightTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Intent>
    val viewModel: SetupClockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    // You might want to check if the permission is now granted
                    val alarmPermissionGranted = Settings.System.canWrite(this)
                    Log.i("DEBUG", "Permission state changed: $alarmPermissionGranted")
                    viewModel.handlePermissionResult(alarmPermissionGranted)
                }
            }

        setContent {
            WakeUpRightTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupClockScreen(viewModel)
                }
            }
        }

        // Using repeatOnLifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.permissionRequest.collect { event ->

                    val permission = event?.getContentIfNotHandled()
                    if (permission != null && permission == android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
                        Log.d("testing", "Navigating to settings for permission.")
                        // Intent to open the specific App Info page in Settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }
                        permissionLauncher.launch(intent)
                    }
                }
            }

        }
    }

}