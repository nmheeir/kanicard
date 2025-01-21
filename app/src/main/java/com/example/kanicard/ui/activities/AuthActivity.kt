@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.example.kanicard.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kanicard.ui.screen.auth.AuthScreen
import com.example.kanicard.ui.theme.KaniCardTheme
import com.example.kanicard.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaniCardTheme {
                val navController = rememberNavController()

                val windowWidthSize = calculateWindowSizeClass(this).widthSizeClass
                val isExpandedScreen = windowWidthSize == WindowWidthSizeClass.Expanded

                Surface(
                    modifier = Modifier
                        .safeDrawingPadding()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "auth"
                    ) {
                        composable(
                            route = "auth"
                        ) {
                            AuthScreen(
                                navController = navController,
                                isExpandScreen = isExpandedScreen,
                                onLoginSuccess = {
                                    startNewActivity(MainActivity::class.java)
                                }
                            )
                        }

                        composable(
                            route = "forgot"
                        ) {
                            Text(
                                text = "Forgot Password"
                            )
                        }
                    }
                }

            }
        }
    }
}