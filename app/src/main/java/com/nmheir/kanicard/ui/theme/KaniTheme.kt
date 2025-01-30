package com.nmheir.kanicard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.ui.theme.colorsScheme.BaseColorScheme
import com.nmheir.kanicard.ui.theme.colorsScheme.GreenAppleColorScheme
import com.nmheir.kanicard.ui.theme.colorsScheme.KaniColorScheme
import com.nmheir.kanicard.ui.theme.colorsScheme.LavenderColorScheme
import com.nmheir.kanicard.ui.theme.colorsScheme.MonetColorScheme

@Composable
fun KaniTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getThemeColorScheme(appTheme, darkTheme),
        content = content
    )
}

@Composable
@ReadOnlyComposable
private fun getThemeColorScheme(
    appTheme: AppTheme,
    darkTheme: Boolean
): ColorScheme {
    val colorScheme = if (appTheme == AppTheme.MONET) {
        MonetColorScheme(LocalContext.current)
    } else {
        colorSchemes.getOrDefault(appTheme, KaniColorScheme)
    }
    return colorScheme.getColorScheme(darkTheme)
}


private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to KaniColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
//        AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
//        AppTheme.NORD to NordColorScheme,
//        AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
//        AppTheme.TAKO to TakoColorScheme,
//        AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
//        AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
//        AppTheme.YINYANG to YinYangColorScheme,
//        AppTheme.YOTSUBA to YotsubaColorScheme,
)