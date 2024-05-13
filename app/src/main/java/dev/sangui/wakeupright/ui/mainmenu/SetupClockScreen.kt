package dev.sangui.wakeupright.ui.mainmenu

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.java.KoinJavaComponent.inject

@Composable
fun SetupClockScreen() {
    val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text("Hour")
        Spacer(modifier = Modifier.height(25.dp))
        NumberPicker(numbers = 24)
        Spacer(modifier = Modifier.height(25.dp))
        Text("Minutes")
        NumberPicker(numbers = 60)
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

@Preview(showBackground = true)
@Composable
fun PreviewSetAlarm() {
    SetupClockScreen()
}