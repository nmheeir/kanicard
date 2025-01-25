@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.nmheir.kanicard.ui.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nmheir.kanicard.ui.navigation.authNavigationBuilder
import com.nmheir.kanicard.ui.theme.KaniCardTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject
    lateinit var client: SupabaseClient

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        client.handleDeeplinks(intent)

        setContent {
            KaniCardTheme {
                val navController = rememberNavController()

                val windowWidthSize = calculateWindowSizeClass(this).widthSizeClass

                Surface(
                    modifier = Modifier
                ) {
                    val windowInset = WindowInsets.systemBars
                    val density = LocalDensity.current
                    val topInset = with(density) { windowInset.getTop(density).toDp() }
                    val authActivityWindowInset = remember {
                        windowInset.only(WindowInsetsSides.Top)
                            .add(WindowInsets(left = 12.dp, right = 12.dp))
                    }
                    CompositionLocalProvider(
                        LocalAuthActivityWindowInset provides authActivityWindowInset
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "sign_in",
                            enterTransition = {
                                fadeIn(tween(250)) + slideInHorizontally { it / 2 }
                            },
                            exitTransition = {
                                fadeOut(tween(250)) + slideOutHorizontally { -it / 2 }
                            },
                            popEnterTransition = {
                                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
                            },
                            popExitTransition = {
                                fadeOut(tween(250)) + slideOutHorizontally { it / 2 }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            authNavigationBuilder(navController)
                        }
                    }
                }

            }
        }
    }
}

val LocalAuthActivityWindowInset =
    compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }