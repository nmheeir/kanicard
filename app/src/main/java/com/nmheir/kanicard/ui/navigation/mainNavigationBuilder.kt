package com.nmheir.kanicard.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.screen.HomeScreen
import com.nmheir.kanicard.ui.screen.ProfileScreen
import com.nmheir.kanicard.ui.screen.more.MoreScreen
import com.nmheir.kanicard.ui.screen.onboarding.OnboardingScreen
import com.nmheir.kanicard.ui.screen.settings.screen.SettingAppearanceScreen
import com.nmheir.kanicard.ui.screen.settings.screen.SettingScreen
import com.nmheir.kanicard.ui.screen.statistics.StatisticsScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mainNavigationBuilder(
    navController: NavHostController,
    topAppBarScrollBehavior: TopAppBarScrollBehavior
) {
    composable(Screens.Home.route) {
        HomeScreen(navController = navController)
    }

    composable(Screens.Statistics.route) {
        StatisticsScreen()
    }

    composable(Screens.Profile.route) {
        ProfileScreen(navController = navController, scrollBehavior = topAppBarScrollBehavior)
    }

    composable(
        route = "search/{query}",
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

    composable(
        route = Screens.More.route
    ) {
        MoreScreen(navController)
    }

    //After sign in
    composable(
        route = "onboarding"
    ) {
        OnboardingScreen(navController)
    }

    /*Setting*/
    composable(Screens.Setting.route) {
        SettingScreen(topAppBarScrollBehavior, navController)
    }

    composable(
        route = "settings/appearance"
    ) {
        SettingAppearanceScreen(topAppBarScrollBehavior, navController)
    }

    composable(
        route = "settings/security"
    ) {
        Text("Security")
    }

    composable(
        route = "settings/advanced"
    ) {
        Text("Advanced")
    }

    composable(
        route = "settings/about"
    ) {
        Text("About")
    }

    composable(
        route = "help"
    ) {
        Text("Help")
    }
}