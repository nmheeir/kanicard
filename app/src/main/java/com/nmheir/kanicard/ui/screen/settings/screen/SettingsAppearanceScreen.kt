package com.nmheir.kanicard.ui.screen.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.AppThemeKey
import com.nmheir.kanicard.constants.DateFormatKey
import com.nmheir.kanicard.constants.ThemeModeKey
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.domain.ui.model.ThemeMode
import com.nmheir.kanicard.core.domain.ui.model.setAppCompatDelegateThemeMode
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.widget.AppThemeModePreferenceWidget
import com.nmheir.kanicard.ui.component.widget.AppThemePreferenceWidget
import com.nmheir.kanicard.ui.component.widget.PreferenceGroupHeader
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.utils.rememberEnumPreference
import com.nmheir.kanicard.utils.rememberPreference
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAppearanceScreen(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.pref_category_appearance),
                scrollBehavior = scrollBehavior,
                onBack = navController::navigateUp
            )
        }
    ) { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding
        ) {
            item { PreferenceGroupHeader(stringResource(R.string.pref_category_theme)) }

            item { GetThemeGroup() }

            item { PreferenceGroupHeader(stringResource(R.string.pref_category_display)) }

//            item { GetDisplayGroup() }
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
                setAppCompatDelegateThemeMode(it)
                onThemeModeChange(it)
            }
        )

        AppThemePreferenceWidget(
            value = appTheme,
            onItemClick = { appTheme ->
                onAppThemeChange(appTheme)
            }
        )
    }
}

@Composable
private fun GetDisplayGroup() {
    val now = remember { LocalDate.now() }

    val dateFormat = rememberPreference(DateFormatKey, DateFormats.first())

    Column {
        TextPreferenceWidget(title = stringResource(R.string.pref_app_language))
        /*        ListPreferenceWidget(
                    title = stringResource(R.string.pref_date_format),
                    subtitle =
                ) { }*/
    }
}

private val DateFormats = listOf(
    "", // Default
    "MM/dd/yy",
    "dd/MM/yy",
    "yyyy-MM-dd",
    "dd MMM yyyy",
    "MMM dd, yyyy",
)
