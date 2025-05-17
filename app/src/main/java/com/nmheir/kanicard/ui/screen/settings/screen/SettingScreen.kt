package com.nmheir.kanicard.ui.screen.settings.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.screen.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) {
    val screens = Screens.SettingsScreen.screens
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.setting),
                scrollBehavior = topAppBarScrollBehavior,
                onBack = navController::navigateUp
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding
        ) {
            itemsIndexed(
                items = screens,
                key = { _, item -> item.hashCode() }
            ) { _, item ->
                val contentColor = LocalContentColor.current
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    TextPreferenceWidget(
                        modifier = Modifier,
                        title = stringResource(item.titleRes),
                        icon = item.iconRes,
                        onPreferenceClick = { navController.navigate(item.route) },
                    )
                }
            }
        }
    }
}