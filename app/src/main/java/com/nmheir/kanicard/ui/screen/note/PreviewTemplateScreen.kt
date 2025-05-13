@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.viewmodels.PreviewTemplateViewModel
import com.nmheir.kanicard.ui.viewmodels.TemplatePreview
import kotlinx.coroutines.launch

@Composable
fun PreviewTemplateScreen(
    navController: NavHostController,
    viewModel: PreviewTemplateViewModel = hiltViewModel()
) {
    val templates = viewModel.templateParse

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { templates.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScrollableTabRow(
                        selectedTabIndex = 0,
                        edgePadding = 0.dp
                    ) {
                        templates.fastForEachIndexed { index, template ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(
                                        text = template.name,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                }
            )
        }
    ) { pv ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.padding(pv)
        ) { page ->
            PreviewContent(
                template = templates[page]
            )
        }
    }
}

@Composable
private fun PreviewContent(
    template: TemplatePreview
) {
    var reverseLayout by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = { reverseLayout = !reverseLayout }
            ) {
                Text(
                    text = stringResource(
                        if (reverseLayout) R.string.action_show_front else R.string.action_show_back
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier.padding(pv)
        ) {

        }
    }
}