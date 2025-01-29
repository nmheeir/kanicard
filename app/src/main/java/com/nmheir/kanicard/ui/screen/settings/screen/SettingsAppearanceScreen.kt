package com.nmheir.kanicard.ui.screen.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.AppThemeKey
import com.nmheir.kanicard.constants.ThemeModeKey
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.domain.ui.model.ThemeMode
import com.nmheir.kanicard.ui.component.widget.AppThemeModePreferenceWidget
import com.nmheir.kanicard.ui.component.widget.AppThemePreferenceWidget
import com.nmheir.kanicard.ui.component.widget.PreferenceGroupHeader
import com.nmheir.kanicard.utils.rememberEnumPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAppearanceScreen(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.pref_category_appearance))
                },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding
        ) {
            item { PreferenceGroupHeader(stringResource(R.string.pref_category_theme)) }

            item { GetThemeGroup() }
        }
    }
}

@Composable
private fun GetThemeGroup(modifier: Modifier = Modifier) {
    val (appTheme, onAppThemeChange) = rememberEnumPreference(AppThemeKey, AppTheme.DEFAULT)
    val (themeMode, onThemeModeChange) = rememberEnumPreference(ThemeModeKey, ThemeMode.SYSTEM)

    Column(modifier = modifier) {
        AppThemeModePreferenceWidget(
            value = themeMode,
            onItemClick = {
                onThemeModeChange(it)
            }
        )

        AppThemePreferenceWidget(value = appTheme, onItemClick = { onAppThemeChange(it) })
    }
}
