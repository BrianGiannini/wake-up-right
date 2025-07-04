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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.sangui.wakeupright.alarm.DataStoreManager
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val snackbarMessage by setupClockViewModel.snackbarMessage.collectAsState()
    val scheduledTime =
        scheduledDate?.let {
            SimpleDateFormat("EEEE HH:mm", Locale.getDefault()).format(it.time)
        } ?: noAlarmText
    val scrollState = rememberScrollState()
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        notificationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Notification permission denied, please enable it in the app settings",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Hours", style = TextStyle(fontSize = 26.sp))
                NumberPicker(
                    dataStoreManager = dataStoreManager,
                    id = "hours",
                    maxNumbers = 24,
                    onValueChange = { selectedHour = it },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Minutes", style = TextStyle(fontSize = 26.sp))
                NumberPicker(
                    dataStoreManager = dataStoreManager,
                    id = "minutes",
                    maxNumbers = 60,
                    incrementNumber = 5,
                    onValueChange = { selectedMinute = it },
                )
            }

            // Buttons and Scheduled Time
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .weight(1f, fill = true)
                    .heightIn(min = 56.dp) // Ensure minimum height for the button

                Button(
                    enabled = scheduledTime == noAlarmText,
                    modifier = buttonModifier,
                    onClick = {
                        if (notificationPermissionGranted) {
                            setupClockViewModel.scheduleAlarm(selectedHour, selectedMinute)
                            setupClockViewModel.showSnackbar("Alarm Scheduled")
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(POST_NOTIFICATIONS)
                            } else {
                                setupClockViewModel.showSnackbar(
                                    "Notification permission is required to schedule the alarm",
                                )
                            }
                        }
                    },
                ) {
                    Text(
                        fontSize = 28.sp,
                        text = "Sleep",
                        textAlign = TextAlign.Center,
                    )
                }

                Button(
                    enabled = scheduledTime != noAlarmText,
                    modifier = buttonModifier,
                    onClick = {
                        setupClockViewModel.cancelAlarm(context)
                        setupClockViewModel.showSnackbar("Alarm Cancelled")
                    },
                ) {
                    Text(
                        fontSize = 28.sp,
                        text = "Cancel",
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = scheduledTime,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .height(40.dp),
                        onClick = {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(
                                    RingtoneManager.EXTRA_RINGTONE_TYPE,
                                    RingtoneManager.TYPE_ALARM,
                                )
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Ringtone")
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                putExtra(
                                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                                )
                            }
                            ringtonePickerLauncher.launch(intent)
                        },
                    ) {
                        val ringDesign: Painter = rememberVectorPainter(image = Icons.Filled.Notifications)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color.Gray, shape = CircleShape),
                        ) {
                            Icon(
                                painter = ringDesign,
                                contentDescription = "Ring",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
        snackbarMessage?.let { message ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    IconButton(onClick = { setupClockViewModel.dismissSnackbar() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss")
                    }
                },
            ) {
                Text(message)
            }
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000) // 3 seconds
                setupClockViewModel.dismissSnackbar()
            }
        }
    }
}
