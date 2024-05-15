package dev.sangui.wakeupright.ui.mainmenu

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sangui.wakeupright.alarm.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun SetupClockScreen(setupClockViewModel: SetupClockViewModel, dataStoreManager: DataStoreManager) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var selectedHour by remember { mutableIntStateOf(0) }
            var selectedMinute by remember { mutableIntStateOf(0) }
            var scheduledTime by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(setupClockViewModel) {
                setupClockViewModel.scheduledDate.collectLatest { date ->
                    scheduledTime = date?.format(DateTimeFormatter.ofPattern("EEEE HH:mm", Locale.getDefault()))
                }
            }

            // Top Elements
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hour", style = TextStyle(fontSize = 36.sp))
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
                    .weight(1f), // Ajouter ceci pour que la colonne prenne l'espace restant
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // To distribute buttons evenly
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Ajouter ceci pour que le bouton prenne l'espace restant verticalement
                        .padding(vertical = 8.dp),
                    onClick = {
                        setupClockViewModel.scheduleAlarm(selectedHour, selectedMinute)
                    },
                ) {
                    Text(
                        fontSize = 24.sp,
                        text = "Sleep",
                        textAlign = TextAlign.Center,
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Ajouter ceci pour que le bouton prenne l'espace restant verticalement
                        .padding(vertical = 8.dp),
                    onClick = {
                        setupClockViewModel.cancelAlarm()
                    },
                ) {
                    Text(
                        fontSize = 24.sp,
                        text = "Cancel",
                    )
                }

                if (scheduledTime != null) {
                    Text(
                        text = "Alarm: $scheduledTime",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                    )
                } else {
                    Text(
                        text = "No alarm scheduled",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}