package com.example.kanicard.ui.screen.statistics

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kanicard.ui.activities.LocalAwareWindowInset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(modifier: Modifier = Modifier) {

    BoxWithConstraints(
        modifier = modifier
            .padding(LocalAwareWindowInset.current.asPaddingValues())
    ) {
        val width = maxWidth
    }
}