package com.nmheir.kanicard.ui.screen.more

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.data.entities.Profile
import com.nmheir.kanicard.ui.activities.AuthActivity
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.LogoHeader
import com.nmheir.kanicard.ui.component.image.CoilImage
import com.nmheir.kanicard.ui.component.shimmer.ShimmerHost
import com.nmheir.kanicard.ui.component.shimmer.TextPlaceholder
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.viewmodels.MainState
import com.nmheir.kanicard.ui.viewmodels.MoreViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents
import com.nmheir.kanicard.utils.startNewActivity

@Composable
fun MoreScreen(
    navController: NavHostController,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is MainState.Error -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            MainState.Success -> {
                (context as? Activity)?.startNewActivity(AuthActivity::class.java)
            }
        }
    }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        DefaultDialog(
            onDismiss = { showLogoutDialog = false },
            buttons = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
                TextButton(onClick = viewModel::signOut) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        ) {
            Text(text = stringResource(R.string.confirm_logout))
            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
    ) { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding
        ) {
            item {
                LogoHeader()
            }

            item {
                if (isLoading) {
                    ShimmerHost(
                        modifier = Modifier.animateItem()
                    ) {
                        TextPlaceholder(
                            height = 36.dp,
                            modifier = Modifier
                                .padding(12.dp)
                                .width(width = 250.dp),
                        )
                    }
                } else {
                    profile?.let {
                        UserProfile(
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route) {
                                    navController.saveState()
                                    restoreState = true
                                    launchSingleTop = true
                                }
                            },
                            profile = it
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.setting),
                    icon = R.drawable.ic_setting,
                    onPreferenceClick = { navController.navigate(Screens.Setting.route) }
                )
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.pref_category_about),
                    icon = R.drawable.ic_info,
                    onPreferenceClick = { }
                )
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.support),
                    icon = R.drawable.ic_help,
                    onPreferenceClick = { }
                )
            }

            item { HorizontalDivider() }

            item {
                TextPreferenceWidget(
                    title = stringResource(R.string.logout),
                    icon = R.drawable.ic_logout,
                    onPreferenceClick = { showLogoutDialog = true }
                )
            }
        }
    }
}


@Composable
private fun UserProfile(
    onProfileClick: () -> Unit = {},
    profile: Profile
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = { onProfileClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(MaterialTheme.padding.small)
        ) {
            CoilImage(
                imageUrl = "",
                modifier = Modifier
                    .size(56.dp)
            )
            Gap(MaterialTheme.padding.small)
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = profile.userName ?: ""
                )
                Text(
                    text = profile.email ?: ""
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    UserProfile(
        onProfileClick = { },
        profile = Profile(
            uid = "uid",
            userName = "userName",
            bio = "bio",
            avatarUrl = "avatarUrl",
            email = "email"
        )
    )
}