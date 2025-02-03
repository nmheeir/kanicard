package com.nmheir.kanicard.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCardScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {

    Text(
        text = "Add new card"
    )

}