package com.nmheir.kanicard.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nmheir.kanicard.constants.RESET_PASSWORD_LINK
import com.nmheir.kanicard.ui.screen.auth.ForgotPasswordScreen
import com.nmheir.kanicard.ui.screen.auth.ResetPasswordScreen
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

    composable(route = "success") {

    }

    composable(
        route = "reset_password",
        deepLinks = listOf(
            navDeepLink {
                uriPattern =
                    "https://kanicard.supabase.com"
            }
        )
    ) {
        ResetPasswordScreen(navController)
    }
}