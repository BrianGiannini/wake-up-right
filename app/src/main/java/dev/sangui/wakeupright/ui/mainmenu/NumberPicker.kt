package dev.sangui.wakeupright.ui.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NumberPicker(
    range: IntRange,
    selectedNumber: MutableState<Int>
) {
    LazyColumn(
        modifier = Modifier.height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(range.toList()) { number ->
            Box(
                modifier = Modifier
                    .background(if (selectedNumber.value == number) Color.LightGray else Color.Transparent)
                    .width(50.dp)
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    modifier = Modifier
                        .background(Color.Transparent)
                        .height(50.dp)
                        .clickable { selectedNumber.value = number }
                )
            }
        }
    }
}
