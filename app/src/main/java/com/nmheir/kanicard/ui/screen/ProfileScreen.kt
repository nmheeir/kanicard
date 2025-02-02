package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.data.entities.Profile
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.image.CoilImage
import com.nmheir.kanicard.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    val currentBackStack by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.profile),
                scrollBehavior = scrollBehavior,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    ) { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding
        ) {
            profile?.let {
                item { UserProfile(profile = it) }
            }
        }
    }
}

@Composable
private fun UserProfile(
    modifier: Modifier = Modifier,
    profile: Profile
) {
    Box(modifier = modifier) {
        Row {
            CoilImage(
                imageUrl = profile.avatarUrl ?: "",
                modifier = Modifier
            )

            Column {
                Text(text = profile.userName ?: "")
                Text(text = profile.email ?: "")
            }

            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null
                )
            }
        }
    }
}