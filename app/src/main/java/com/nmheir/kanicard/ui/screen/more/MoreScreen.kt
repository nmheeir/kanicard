package com.nmheir.kanicard.ui.screen.more

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.LogoHeader
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.viewmodels.MainState
import com.nmheir.kanicard.ui.viewmodels.MoreViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents

@Composable
fun MoreScreen(
    navController: NavHostController,
    viewModel: MoreViewModel = hiltViewModel()
) {

    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is MainState.Error -> {

            }

            MainState.Success -> TODO()
        }
    }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        DefaultDialog(
            onDismiss = { showLogoutDialog = false },
            buttons = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
                TextButton(onClick = viewModel::signOut) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        ) {
            Text(text = stringResource(R.string.confirm_logout))
            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    Scaffold { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding
        ) {
            item {
                LogoHeader()
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.setting),
                    icon = R.drawable.ic_setting,
                    onPreferenceClick = { navController.navigate(Screens.Setting.route) }
                )
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.pref_category_about),
                    icon = R.drawable.ic_info,
                    onPreferenceClick = { }
                )
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.support),
                    icon = R.drawable.ic_help,
                    onPreferenceClick = { }
                )
            }

            item { HorizontalDivider() }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.logout),
                    icon = R.drawable.ic_logout,
                    onPreferenceClick = { showLogoutDialog = true }
                )
            }
        }
    }

}