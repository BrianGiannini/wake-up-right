package dev.sangui.wakeupright.ui.mainmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sangui.wakeupright.alarm.DataStoreManager


@Composable
fun SetupClockScreen(setupClockViewModel: SetupClockViewModel, dataStoreManager: DataStoreManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text("Hour", style = TextStyle(fontSize = 36.sp))
        Spacer(modifier = Modifier.height(25.dp))
        NumberPicker(dataStoreManager = dataStoreManager, id = "hours", maxNumbers = 24)
        Spacer(modifier = Modifier.height(25.dp))
        Text("Minutes", style = TextStyle(fontSize = 36.sp))
        NumberPicker(dataStoreManager = dataStoreManager, id = "minutes", maxNumbers = 60, incrementNumber = 5)
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            modifier = Modifier.size(200.dp),
            onClick = {
                setupClockViewModel.scheduleAlarm()
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
            modifier = Modifier.size(200.dp),
            onClick = {
                setupClockViewModel.cancelAlarm()
            },
        ) {
            Text(
                fontSize = 35.sp,
                text = "Cancel",
            )
        }
    }

}
