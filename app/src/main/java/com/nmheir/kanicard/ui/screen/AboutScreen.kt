package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nmheir.kanicard.BuildConfig
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.ui.component.LogoHeader
import com.nmheir.kanicard.ui.component.TopAppBar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavHostController,
    topAppBarScrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.pref_category_about),
                onBack = navController::navigateUp,
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { contentPadding ->
        ScrollbarLazyColumn(
            modifier = Modifier.padding(contentPadding)
        ) {
            item { LogoHeader() }

        }
    }
}