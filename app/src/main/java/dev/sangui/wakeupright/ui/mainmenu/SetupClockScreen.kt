package dev.sangui.wakeupright.ui.mainmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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


@Composable
fun SetupClockScreen(setupClockViewModel: SetupClockViewModel, dataStoreManager: DataStoreManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var selectedHour by remember { mutableIntStateOf(0) }
        var selectedMinute by remember { mutableIntStateOf(0) }
        var scheduledTime by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(setupClockViewModel) {
            setupClockViewModel.scheduledDate.collectLatest { date ->
                scheduledTime = date?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            }
        }

        Text("Hour", style = TextStyle(fontSize = 36.sp))
        Spacer(modifier = Modifier.height(25.dp))
        NumberPicker(
            dataStoreManager = dataStoreManager,
            id = "hours",
            maxNumbers = 24,
            onValueChange = { selectedHour = it }
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text("Minutes", style = TextStyle(fontSize = 36.sp))
        NumberPicker(
            dataStoreManager = dataStoreManager,
            id = "minutes",
            maxNumbers = 60,
            incrementNumber = 5,
            onValueChange = { selectedMinute = it }
        )
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            modifier = Modifier
                .height(150.dp)
                .width(200.dp),
            onClick = {
                setupClockViewModel.scheduleAlarm(selectedHour, selectedMinute)
            },
        ) {
            Text(
                fontSize = 35.sp,
                lineHeight = 35.sp,
                text = "Wake me up",
                textAlign = TextAlign.Center,
            )
        }

        Button(
            modifier = Modifier
                .height(150.dp)
                .width(200.dp),
            onClick = {
                setupClockViewModel.cancelAlarm()
            },
        ) {
            Text(
                fontSize = 35.sp,
                text = "Cancel",
            )
        }

        if (scheduledTime != null) {
            Text(
                text = "Alarm scheduled at $scheduledTime",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Text(
                text = "No alarm scheduled",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

        }
    }

}
