package dev.sangui.wakeupright.ui.mainmenu

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sangui.wakeupright.alarm.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

@Composable
fun NumberPicker(
    dataStoreManager: DataStoreManager,
    id: String,
    maxNumbers: Int,
    incrementNumber: Int = 1,
    onValueChange: (Int) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        val contentPadding = (maxWidth - 50.dp) / 2
        val itemSpacing = (maxWidth - contentPadding * 2) / 5
        val processMax = maxNumbers / incrementNumber
        val pagerState = rememberPagerState(
            pageCount = {
                processMax
            }
        )

        // Load the saved selected number when the composable is initialized
        LaunchedEffect(id) {
            val savedNumber = dataStoreManager.selectedNumberFlow(id).first()
            pagerState.scrollToPage(savedNumber / incrementNumber)
        }

        // Save the selected number whenever the current page changes
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                Log.d("NumberPicker", "saveSelectedNumber: $id, ${page * incrementNumber}")
                dataStoreManager.saveSelectedNumber(id, page * incrementNumber)
                onValueChange(page * incrementNumber)
            }
        }

        HorizontalPager(
            state = pagerState,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(5) // 5 for the snapping scrolling effect
            ),
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = itemSpacing,
        ) { page ->
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction).absoluteValue
                        val percentFromCenter = 1.0f - (pageOffset / (5f / 2f))
                        val opacity = 0.25f + (percentFromCenter * 0.75f).coerceIn(0f, 1f)
                        alpha = opacity
                        clip = true
                    }
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "${page * incrementNumber}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
fun NumberPickerPreview() {
    NumberPicker(
        dataStoreManager = DataStoreManager(LocalContext.current),
        id = "id",
        maxNumbers = 100,
        incrementNumber = 10,
        onValueChange = {},
    )
}