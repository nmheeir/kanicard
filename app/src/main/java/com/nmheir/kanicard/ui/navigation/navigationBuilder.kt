package com.nmheir.kanicard.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nmheir.kanicard.ui.screen.BrowseCardScreen
import com.nmheir.kanicard.ui.screen.DeckDetailScreen
import com.nmheir.kanicard.ui.screen.HomeScreen
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.screen.note.NoteEditorScreen
import com.nmheir.kanicard.ui.screen.note.NoteTemplateScreen
import com.nmheir.kanicard.ui.screen.onboarding.OnboardingScreen
import com.nmheir.kanicard.ui.screen.settings.BackupRestoreScreen
import com.nmheir.kanicard.ui.screen.settings.screen.SettingAppearanceScreen
import com.nmheir.kanicard.ui.screen.settings.screen.SettingScreen
import com.nmheir.kanicard.ui.screen.settings.screen.SettingsAdvancedScreen
import com.nmheir.kanicard.ui.screen.statistics.StatisticsScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    topAppBarScrollBehavior: TopAppBarScrollBehavior
) {

    composable(Screens.MainScreen.Home.route) {
//        TestScreen()
        HomeScreen(navController = navController)
    }

    composable(Screens.MainScreen.Statistics.route) {
        StatisticsScreen()
    }

    composable(
        route = "${Screens.Base.Search}/{query}",
        arguments = listOf(
            navArgument("query") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query")

        Text(
            text = query ?: "Không có query", // Xử lý trường hợp query null
            style = MaterialTheme.typography.bodyLarge
        )
    }

    //After sign in
    composable(
        route = Screens.Base.Onboarding.route
    ) {
        OnboardingScreen(navController)
    }

    /*Card*/
    composable(
        route = "${Screens.Base.NoteEditor.route}?deckId={deckId}",
        arguments = listOf(
            navArgument("deckId") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) {
        NoteEditorScreen(navController)
    }

    composable(
        route = "{type}/${Screens.Base.Templates.route}",
        arguments = listOf(
            navArgument("type") {
                type = NavType.LongType
            }
        )
    ) {
        NoteTemplateScreen(navController)
    }

    composable(
        route = "${Screens.Base.Deck.route}/{deckId}",
        arguments = listOf(
            navArgument("deckId") {
                type = NavType.LongType
            }
        )
    ) {
        DeckDetailScreen(navController, topAppBarScrollBehavior)
    }

    composable(
        route = "{deckId}/${Screens.Base.BrowseCard.route}",
        arguments = listOf(
            navArgument("deckId") {
                type = NavType.LongType
            }
        )
    ) {
        BrowseCardScreen(navController)
    }

    composable(
        route = "${Screens.Base.Learn.route}/{deckId}",
        arguments = listOf(
            navArgument("deckId") {
                type = NavType.LongType
            }
        )
    ) {
        Text(text = "Learn Screen")
    }


    /*Setting*/
    composable(Screens.SettingsScreen.Setting.route) {
        SettingScreen(topAppBarScrollBehavior, navController)
    }

    composable(
        route = Screens.SettingsScreen.Appearance.route
    ) {
        SettingAppearanceScreen(topAppBarScrollBehavior, navController)
    }

    composable(
        route = Screens.SettingsScreen.SecurityPrivacy.route
    ) {
        Text("Security")
    }

    composable(
        route = Screens.SettingsScreen.Advanced.route
    ) {
        SettingsAdvancedScreen(navController, topAppBarScrollBehavior)
    }

    composable(
        route = Screens.SettingsScreen.About.route
    ) {
        Text("about")
    }

    composable(
        route = Screens.SettingsScreen.Help.route
    ) {
        Text("Help")
    }

    composable(
        route = Screens.SettingsScreen.BackupRestore.route
    ) {
        BackupRestoreScreen(navController)
    }
}