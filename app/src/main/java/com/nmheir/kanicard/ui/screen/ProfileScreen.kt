package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .padding(LocalAwareWindowInset.current.asPaddingValues())
            .verticalScroll(rememberScrollState())
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {

        for (i in 1..50) {
            Text(
                text = "Profile $i",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }

    TopAppBar(
        title = { Text(text = stringResource(R.string.profile)) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun UserSection(modifier: Modifier = Modifier) {

    Row {

    }

}