package dev.sangui.wakeupright.ui.mainmenu


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sangui.wakeupright.MainActivity
import dev.sangui.wakeupright.alarm.AlarmItem
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime

@Composable
fun SetupClockScreen(viewModel: SetupClockViewModel = viewModel()) {
//    val setupClockViewModel: SetupClockViewModel by inject(SetupClockViewModel::class.java)

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
                viewModel.checkAndRequestPermission()
            },
        ) {
            Text(text = "Wake me up")
        }

        Button(
            onClick = {
                // Debugging: Force a state change
                viewModel.debugTriggerPermissionRequest()
            }
        ) {
            Text("Debug: Request Permission")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSetAlarm() {
    SetupClockScreen()
}