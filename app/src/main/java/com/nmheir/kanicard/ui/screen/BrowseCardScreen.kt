@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.viewmodels.BrowseCardViewModel

@Composable
fun BrowseCardScreen(
    navController: NavHostController,
    viewModel: BrowseCardViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Browse card")
                },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                }
            )
        }
    ) { pv ->
        // TODO: Table Content

        Column(
            modifier = Modifier.padding(pv)
        ) {
        }
    }
}