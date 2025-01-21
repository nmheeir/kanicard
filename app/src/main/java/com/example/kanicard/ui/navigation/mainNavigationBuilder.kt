package com.example.kanicard.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.kanicard.ui.screen.Screens
import com.example.kanicard.ui.screen.home.HomeScreen
import com.example.kanicard.ui.screen.setting.SettingScreen
import com.example.kanicard.ui.screen.statistics.StatisticsScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mainNavigationBuilder(
    navController: NavHostController,
    appBarScrollBehavior: TopAppBarScrollBehavior,
    topAppBarScrollBehavior: TopAppBarScrollBehavior
) {
    composable(Screens.Home.route) {
        HomeScreen(
            navController = navController,
            appBarScrollBehavior = appBarScrollBehavior
        )
    }

    composable(Screens.Setting.route) {
        SettingScreen(topAppBarScrollBehavior, navController)
    }

    composable(Screens.Statistics.route) {
        StatisticsScreen()
    }

    composable(Screens.Profile.route) {
        Text(text = "Profile Screen")
    }
}