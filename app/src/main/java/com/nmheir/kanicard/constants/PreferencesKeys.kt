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
