package dev.sangui.wakeupright.ui.mainmenu

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sangui.wakeupright.alarm.DataStoreManager
import org.koin.java.KoinJavaComponent.inject

@Composable
fun SetupClockScreen() {
    val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)
    val dataStoreManager: DataStoreManager by inject(DataStoreManager::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text("Hour")
        Spacer(modifier = Modifier.height(25.dp))
        NumberPicker(dataStoreManager = dataStoreManager, numbers = 24, id = "hours")
        Spacer(modifier = Modifier.height(25.dp))
        Text("Minutes")
        NumberPicker(dataStoreManager = dataStoreManager, numbers = 60, id = "minutes")
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                setupClockViewModel.scheduleAlarm()
            },
        ) {
            Text(text = "Wake me up")
        }

        Button(
            onClick = {
                setupClockViewModel.cancelAlarm()
            },
        ) {
            Text(text = "cancel")
        }
    }

}
