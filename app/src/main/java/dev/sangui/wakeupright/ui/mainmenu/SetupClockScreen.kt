package dev.sangui.wakeupright.ui.mainmenu

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmScheduler
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun SetupClockScreen() {
    val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val selectedNumber = remember { mutableIntStateOf(0) }

        Text("Selected Number: ${selectedNumber.intValue}")

        Spacer(modifier = Modifier.height(25.dp))

        NumberPicker(range = 1..24, selectedNumber)

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                showDialog(context, setupClockViewModel)

            },
        ) {
            Text(text = "Wake me up")
        }

    }

}

private fun showDialog(context: Context, setupClockViewModel: SetupClockViewModel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupClockViewModel.setupAlarm(
                AlarmItem(
                    LocalDateTime.now(),
                    ""
                )
            )
        } else {
            AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage("This app requires permission to schedule exact alarms. Please enable it in the settings.")
                .setPositiveButton("Go to Settings") { dialog, which ->
                    // Intent to open app's settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetAlarm() {
    SetupClockScreen()
}