package com.nmheir.kanicard.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.nmheir.kanicard.R

@Immutable
sealed interface Screens {
    @get:StringRes
    val titleRes: Int?

    @get:DrawableRes
    val iconRes: Int?
    val route: String

    // Main module
    sealed class MainScreen(
        @StringRes override val titleRes: Int,
        @DrawableRes override val iconRes: Int,
        override val route: String
    ) : Screens {
        object Home : MainScreen(R.string.home, R.drawable.ic_home, "home")
        object Statistics : MainScreen(R.string.statistics, R.drawable.ic_statistic, "statistics")

        companion object {
            val Screens = listOf(Home, Statistics)
        }
    }

    // Settings module
    sealed class SettingsScreen(
        @StringRes override val titleRes: Int,
        @DrawableRes override val iconRes: Int,
        override val route: String
    ) : Screens {
        object Setting : SettingsScreen(R.string.setting, R.drawable.ic_setting, "setting")
        object Appearance : SettingsScreen(R.string.appearance, R.drawable.ic_palette, "appearance")
        object SecurityPrivacy :
            SettingsScreen(R.string.security_privacy, R.drawable.ic_security, "security_privacy")

        object About : SettingsScreen(R.string.about, R.drawable.ic_info, "about")
        object Advanced : SettingsScreen(R.string.advanced, R.drawable.ic_code, "advanced")
        object Help : SettingsScreen(R.string.help, R.drawable.ic_help, "help")
        data object BackupRestore :
            SettingsScreen(
                R.string.backup_restore,
                R.drawable.ic_settings_backup_restore,
                "backup_restore"
            )

        companion object {
            val screens = listOf(
                Appearance,
                BackupRestore,
                SecurityPrivacy,
                Advanced,
                Help,
                About,
            )
        }
    }

    sealed class Base(
        override val route: String
    ) : Screens {
        // Không có icon, để null
        override val iconRes: Int? = null
        override val titleRes: Int? = null

        data object Onboarding : Base("onboarding")
        data object Search : Base("search")
        data object NoteEditor : Base("note_editor")
        data object Templates : Base("templates")
        data object Deck : Base("deck")
        data object BrowseCard : Base("browse_card")
        data object Learn : Base("learn")
        data object PreviewNote : Base("preview_note")

        data object CompleteLearn : Base("complete_learn")
    }
}