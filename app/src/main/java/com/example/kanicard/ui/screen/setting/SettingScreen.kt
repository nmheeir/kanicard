package com.example.kanicard.ui.screen.setting

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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