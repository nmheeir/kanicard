package com.nmheir.kanicard.ui.screen.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import com.nmheir.kanicard.constants.AppThemeKey
import com.nmheir.kanicard.constants.ThemeModeKey
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.domain.ui.model.ThemeMode
import com.nmheir.kanicard.core.domain.ui.model.setAppCompatDelegateThemeMode
import com.nmheir.kanicard.ui.component.widget.AppThemeModePreferenceWidget
import com.nmheir.kanicard.ui.component.widget.AppThemePreferenceWidget
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.rememberEnumPreference

class ThemeStep : OnboardingStep {
    override val isComplete: Boolean = true

    @Composable
    override fun Content() {

        val (themeMode, onThemeModeChange) = rememberEnumPreference(ThemeModeKey, ThemeMode.SYSTEM)
        val (appTheme, onAppThemeChange) = rememberEnumPreference(AppThemeKey, AppTheme.DEFAULT)


        Column {
            AppThemeModePreferenceWidget(
                value = themeMode,
                onItemClick = {
                    onThemeModeChange(it)
                    setAppCompatDelegateThemeMode(it)
                },
            )

            AppThemePreferenceWidget(
                value = appTheme,
                onItemClick = {
                    onAppThemeChange(it)
                },
            )
        }
    }
}