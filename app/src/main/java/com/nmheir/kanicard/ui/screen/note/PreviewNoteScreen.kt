@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.card.InteractiveFlashcard
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.PreviewNoteViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PreviewNoteScreen(
    navController: NavHostController,
    viewModel: PreviewNoteViewModel = hiltViewModel()
) {

    val datas by viewModel.datas.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(initialPage = 0) {
        datas.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                },
                actions = {
                    IconButtonTooltip(
                        iconRes = R.drawable.ic_bookmark,
                        shortCutDescription = "Bookmark note",
                        onClick = {}
                    )
                }
            )
        },
        bottomBar = {
            PreviewNoteBottomBar(
                pagerState = pagerState
            )
        }
    ) { pv ->
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 3,
            modifier = Modifier.padding(pv)
        ) { page ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val flipController = rememberFlipController()
                InteractiveFlashcard(
                    flipController = flipController,
                    qHtml = datas[page].qHtml,
                    aHtml = datas[page].aHtml,
                    enableFlip = true
                )
            }
        }
    }
}

@Composable
private fun PreviewNoteBottomBar(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    val pageCount = pagerState.pageCount
    val scope = rememberCoroutineScope()

    // Tránh lỗi nếu chưa có dữ liệu trang
    if (pageCount <= 0) return

    // Slider hiển thị 1..pageCount, nhưng pagerState là 0-based
    var sliderValue by remember { mutableFloatStateOf(pagerState.currentPage + 1f) }

    // Đồng bộ sliderValue khi currentPage thay đổi
    LaunchedEffect(pagerState.currentPage) {
        sliderValue = pagerState.currentPage + 1f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
            .padding(bottom = 12.dp)
    ) {
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                scope.launch {
                    val page = sliderValue.roundToInt().coerceIn(1, pageCount)
                    pagerState.animateScrollToPage(page - 1) // Trừ 1 vì 0-based
                }
            },
            steps = (pageCount - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            valueRange = 1f..pageCount.toFloat()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                enabled = pagerState.currentPage > 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            (pagerState.currentPage - 1).coerceAtLeast(0)
                        )
                    }
                }
            ) {
                Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
            }
            Text(
                text = "${pagerState.currentPage + 1} of $pageCount", // +1 để hiển thị 1-based
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(
                enabled = pagerState.currentPage < pageCount - 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            (pagerState.currentPage + 1).coerceAtMost(pageCount - 1)
                        )
                    }
                }
            ) {
                Icon(painterResource(R.drawable.ic_arrow_forward_ios), null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        var sliderValue by remember { mutableFloatStateOf(1f) }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {},
            steps = 10,
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
                inactiveTrackColor = Color.Gray
            ),
            valueRange = 1f..10f
        )
    }
}