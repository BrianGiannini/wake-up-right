package dev.sangui.wakeupright.ui.mainmenu

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.sangui.wakeupright.alarm.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.util.Locale

const val noAlarmText = "No alarm scheduled"

@Composable
fun SetupClockScreen(
    setupClockViewModel: SetupClockViewModel,
    dataStoreManager: DataStoreManager,
    ringtonePickerLauncher: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    val scheduledDate by setupClockViewModel.scheduledDate.collectAsState()
    val scheduledTime = scheduledDate?.format(DateTimeFormatter.ofPattern("EEEE HH:mm", Locale.getDefault())) ?: noAlarmText
    val scrollState = rememberScrollState()
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        notificationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Notification permission denied, please enable it in the app settings",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Elements
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hours", style = TextStyle(fontSize = 36.sp))
                NumberPicker(
                    dataStoreManager = dataStoreManager,
                    id = "hours",
                    maxNumbers = 24,
                    onValueChange = { selectedHour = it }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Minutes", style = TextStyle(fontSize = 36.sp))
                NumberPicker(
                    dataStoreManager = dataStoreManager,
                    id = "minutes",
                    maxNumbers = 60,
                    incrementNumber = 5,
                    onValueChange = { selectedMinute = it }
                )
                Spacer(modifier = Modifier.height(25.dp))
            }

            // Buttons and Scheduled Time
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    enabled = scheduledTime == noAlarmText,
                            modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),

                    onClick = {
                        if (notificationPermissionGranted) {
                            setupClockViewModel.scheduleAlarm(selectedHour, selectedMinute)
                            setupClockViewModel.showToast(context, "Alarm Scheduled")
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(POST_NOTIFICATIONS)
                            } else {
                                setupClockViewModel.showToast(
                                    context,
                                    "Notification permission is required to schedule the alarm"
                                )
                            }
                        }
                    },
                ) {
                    Text(
                        fontSize = 24.sp,
                        text = "Sleep",
                        textAlign = TextAlign.Center,
                    )
                }

                Button(
                    enabled = scheduledTime != noAlarmText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    onClick = {
                        setupClockViewModel.cancelAlarm(context)
                        setupClockViewModel.showToast(context, "Alarm Cancelled")
                    },
                ) {
                    Text(
                        fontSize = 24.sp,
                        text = "Cancel",
                    )
                }

                Button(onClick = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Ringtone")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        putExtra(
                            RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        )
                    }
                    ringtonePickerLauncher.launch(intent)
                }) {
                    Text("Select Ringtone")
                }

                Text(
                    text = scheduledTime,
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}