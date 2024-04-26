package dev.sangui.wakeupright

import android.os.Bundle
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
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    val viewModel: SetupClockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.handlePermissionResult(isGranted)
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
                    Log.d("testing", "Event collected: ${event?.getContentIfNotHandled()}")

                    event?.getContentIfNotHandled()?.let { permission ->
                        Log.d("testing", "Requesting permission for: $permission")

                        permissionLauncher.launch(permission)
                    }
                }
            }
        }

    }

}