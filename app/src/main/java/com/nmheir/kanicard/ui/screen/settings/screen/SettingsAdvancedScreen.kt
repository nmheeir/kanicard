package com.nmheir.kanicard.ui.screen.settings.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAdvancedScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.pref_category_advanced),
                scrollBehavior = scrollBehavior,
                onBack = navController::navigateUp
            )
        }
    ) { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding
        ) {
            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.onboarding_guide),
                    onPreferenceClick = { navController.navigate("onboarding") }
                )
            }
        }
    }
}