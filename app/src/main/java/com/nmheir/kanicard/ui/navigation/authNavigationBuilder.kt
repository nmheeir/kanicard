package com.nmheir.kanicard.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nmheir.kanicard.ui.screen.auth.ForgotPasswordScreen
import com.nmheir.kanicard.ui.screen.auth.SignInScreen
import com.nmheir.kanicard.ui.screen.auth.SignUpScreen

fun NavGraphBuilder.authNavigationBuilder(
    navController: NavHostController,
) {
    composable(
        route = "sign_in"
    ) {
        SignInScreen(navController)
    }

    composable(
        route = "sign_up"
    ) {
        SignUpScreen(navController)
    }

    composable(
        route = "forgot_password"
    ) {
        ForgotPasswordScreen(navController)
    }
}