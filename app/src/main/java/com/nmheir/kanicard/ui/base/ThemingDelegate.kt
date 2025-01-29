package com.nmheir.kanicard.ui.base

import android.app.Activity
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.AppThemeKey
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.utils.enumPreference

interface ThemingDelegate {
    fun applyAppTheme(activity: Activity)

    companion object {
        fun getThemeResIds(appTheme: AppTheme): List<Int> {
            return buildList(2) {
                add(themeResources.getOrDefault(appTheme, R.style.Theme_KaniCard))
            }
        }
    }
}

class ThemingDelegateImpl : ThemingDelegate {
    override fun applyAppTheme(activity: Activity) {
        val appTheme by enumPreference(activity, AppThemeKey, AppTheme.DEFAULT)
        ThemingDelegate.getThemeResIds(appTheme)
            .forEach(activity::setTheme)
    }
}

private val themeResources: Map<AppTheme, Int> = mapOf(
    AppTheme.MONET to R.style.Theme_KaniCard_Monet,
    AppTheme.GREEN_APPLE to R.style.Theme_KaniCard_GreenApple,
    /*    AppTheme.LAVENDER to R.style.Theme_Kani_Lavender,
        AppTheme.MIDNIGHT_DUSK to R.style.Theme_Kani_MidnightDusk,
        AppTheme.NORD to R.style.Theme_Kani_Nord,
        AppTheme.STRAWBERRY_DAIQUIRI to R.style.Theme_Kani_StrawberryDaiquiri,
        AppTheme.TAKO to R.style.Theme_Kani_Tako,
        AppTheme.TEALTURQUOISE to R.style.Theme_Kani_TealTurquoise,
        AppTheme.YINYANG to R.style.Theme_Kani_YinYang,
        AppTheme.YOTSUBA to R.style.Theme_Kani_Yotsuba,
        AppTheme.TIDAL_WAVE to R.style.Theme_Kani_TidalWave,*/
)