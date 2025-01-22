package com.nmheir.kanicard.ui.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) {
    Text(
        text = "Setting Screen"
    )
}