package dev.sangui.wakeupright.ui.mainmenu

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sangui.wakeupright.alarm.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    dataStoreManager: DataStoreManager,
    numbers: Int,
    id: String,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val contentPadding = (maxWidth - 50.dp) / 2
        val offSet = maxWidth / 5
        val itemSpacing = offSet - 50.dp
        val pagerState = rememberPagerState(pageCount = {
            numbers
        })

        // Load the saved selected number when the composable is initialized
        LaunchedEffect(id) {
            val savedNumber = dataStoreManager.selectedNumberFlow(id).first()
            pagerState.scrollToPage(savedNumber)
        }

        // Save the selected number whenever the current page changes
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                Log.d("NumberPicker", "saveSelectedNumber: $id, $page")
                dataStoreManager.saveSelectedNumber(id, page)
            }
        }

        CenterCircle(
            modifier = modifier
                .size(100.dp)
                .align(Alignment.Center),
            fillColor = Color.Red,
            strokeWidth = 2.dp
        )

        HorizontalPager(
            modifier = modifier,
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
                    .size(100.dp)
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction).absoluteValue
                        // Set the item alpha based on the distance from the center
                        val percentFromCenter = 1.0f - (pageOffset / (5f / 2f))
                        val opacity = 0.25f + (percentFromCenter * 0.75f).coerceIn(0f, 1f)

                        alpha = opacity
                        clip = true
                    }) {
                Text(
                    text = "$page",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .size(350.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}