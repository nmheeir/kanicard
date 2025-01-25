package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Column(
        modifier = Modifier
            .padding(LocalAwareWindowInset.current.asPaddingValues())
    ) {
        Text(
            text = stringResource(R.string.profile)
        )
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