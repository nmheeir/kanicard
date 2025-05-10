package com.nmheir.kanicard.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val PauseSearchHistoryKey = booleanPreferencesKey("pauseSearchHistory")

val MaxImageCacheSizeKey = intPreferencesKey("maxImageCacheSize")

val SearchSourceKey = stringPreferencesKey("searchSource")

enum class SearchSource {
    LOCAL, ONLINE;

    fun toggle() = when (this) {
        LOCAL -> ONLINE
        ONLINE -> LOCAL
    }
}

val OnboardingCompleteKey = booleanPreferencesKey("onboarding_complete")
val ThemeModeKey = stringPreferencesKey("themeMode")
val AppThemeKey = stringPreferencesKey("appTheme")
val DateFormatKey = stringPreferencesKey("dateFormat")
val StoragePathKey = stringPreferencesKey("storage_path")

val EmailKey = stringPreferencesKey("email")