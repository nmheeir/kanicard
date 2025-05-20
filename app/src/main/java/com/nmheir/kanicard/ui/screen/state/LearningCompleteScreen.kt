@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.state

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.ui.screen.Screens

@Composable
fun LearningCompleteScreen(
    navController: NavHostController,
) {
    BackHandler {
        navController.navigate(Screens.MainScreen.Home.route) {
            popUpTo(0)
            launchSingleTop = true
        }
    }
    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    navController.navigate(Screens.MainScreen.Home.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            ) {
                Text(
                    text = "Back to Home"
                )
            }
        }
    ) { pv ->
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.mediumSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(pv)
        ) {
            Text(
                text = "Great job !!"
            )
            Text(
                text = "You have completed this deck"
            )
        }
    }
}