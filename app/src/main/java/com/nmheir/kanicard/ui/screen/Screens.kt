package com.nmheir.kanicard.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.nmheir.kanicard.R

@Immutable
sealed class Screens(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
    val route: String
) {
    data object Home : Screens(R.string.home, R.drawable.ic_home, "Home")
    data object Statistics : Screens(R.string.statistics, R.drawable.ic_statistic, "Statistics")
    data object Setting : Screens(R.string.setting, R.drawable.ic_setting, "Setting")
    data object Profile : Screens(R.string.profile, R.drawable.ic_person, "Profile")
    data object Notification :
        Screens(R.string.notification, R.drawable.ic_notification, "Notification")

    data object Logout : Screens(R.string.logout, R.drawable.ic_logout, "Logout")
    data object Support : Screens(R.string.support, R.drawable.ic_support, "Support")

    companion object {
        val MainScreens = listOf(Home, Statistics)
    }
}