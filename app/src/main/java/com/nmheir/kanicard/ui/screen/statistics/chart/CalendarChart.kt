@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.extensions.formatLongDate
import com.nmheir.kanicard.extensions.isToday
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartData
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartItemData
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.StatisticUiAction
import com.nmheir.kanicard.utils.fakeCalendarData
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarChart(
    modifier: Modifier = Modifier,
    data: CalendarChartData,
    year: Int,
    action: (StatisticUiAction) -> Unit
) {
    val lazyRowState = rememberLazyListState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        YearSelect(
            year = year,
            action = action
        )

        LazyRow(
            state = lazyRowState,
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = false,
            flingBehavior = rememberSnapFlingBehavior(lazyRowState, SnapPosition.Start)
        ) {
            // Chuyển Map<Int, List<...>> thành List<Pair<month, items>>
            val monthsList = data.data.toList()
                // optional: sắp xếp lại tháng tăng dần
                .sortedBy { it.first }
            items(
                items = monthsList,
                key = { it.first }
            ) { (month, monthItems) ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tiêu đề tháng (ví dụ "Jan", "Feb", v.v.)
                    Text(
                        text = Month.of(month)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium
                    )
                    // Sử dụng Composable tháng đã viết
                    CalendarChartMonthItem(
                        modifier = Modifier,
                        monthData = monthItems
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 12.dp)
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall
            )
            CalendarChartDayItem(
                color = MaterialTheme.colorScheme.errorContainer,
            )
            (1..4).map {
                CalendarChartDayItem(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = ((it + 1) * 0.2f)),
                )
            }
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun YearSelect(
    modifier: Modifier = Modifier,
    year: Int,
    action: (StatisticUiAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
    ) {
        IconButtonTooltip(
            iconRes = R.drawable.ic_arrow_back,
            shortCutDescription = "Move to previous year",
            onClick = {
                action(StatisticUiAction.ChangeCalendarChartState(year.dec()))
            }
        )

        Gap(12.dp)

        Text(
            text = year.toString(),
            style = MaterialTheme.typography.labelLarge
        )

        Gap(12.dp)

        IconButtonTooltip(
            iconRes = R.drawable.ic_arrow_forward,
            shortCutDescription = "Move to next year",
            enabled = year != LocalDate.now().year,
            onClick = {
                action(StatisticUiAction.ChangeCalendarChartState(year.inc()))
            }
        )
    }
}

@Composable
private fun CalendarChartMonthItem(
    modifier: Modifier = Modifier,
    monthData: List<CalendarChartItemData>
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LazyHorizontalGrid(
            modifier = Modifier
                .height(7.dp * 20 + 6.dp * 6)
                .width(if (monthData.size > 21) 130.dp else 80.dp),
            rows = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(monthData) { data ->
                val tooltipState = rememberTooltipState()
                TooltipBox(
                    state = tooltipState,
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        val day = data.day.formatLongDate()
                        RichTooltip(
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = pluralStringResource(
                                            R.plurals.n_reviews,
                                            data.reviewCount,
                                            data.reviewCount
                                        ),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        )
                    }
                ) {
                    CalendarChartDayItem(
                        data = data,
                        enabled = data.reviewCount > 0,
                        onClick = {
                            scope.launch {
                                if (!tooltipState.isVisible) tooltipState.show() else tooltipState.dismiss()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarChartDayItem(
    modifier: Modifier = Modifier,
    data: CalendarChartItemData,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        data.reviewCount >= 200 -> MaterialTheme.colorScheme.primary
        data.reviewCount >= 100 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        data.reviewCount >= 50 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        data.reviewCount >= 1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.errorContainer
    }

    CalendarChartDayItem(
        color = color,
        showBorder = data.day.isToday(),
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun CalendarChartDayItem(
    modifier: Modifier = Modifier,
    color: Color,
    showBorder: Boolean = false,
    enabled: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color)
            .clickable(enabled = enabled, onClick = onClick)
            .then(
                other = if (showBorder) {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        MaterialTheme.shapes.extraSmall
                    )
                } else {
                    Modifier
                },
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        val month = 5
        val monthData = fakeCalendarData.data[month]!!

        CalendarChart(
            data = fakeCalendarData,
            year = 2025
        ) { }
    }
}