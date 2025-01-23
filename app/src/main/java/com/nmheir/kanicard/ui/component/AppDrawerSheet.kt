package com.nmheir.kanicard.ui.component

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.AuthActivity
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.utils.startNewActivity

@Composable
fun AppDrawerSheet(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        DefaultDialog(
            onDismiss = { showLogoutDialog = false },
            content = {
                Text(
                    text = stringResource(R.string.confirm_logout),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            },
            buttons = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }

                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        (context as? Activity)?.startNewActivity(AuthActivity::class.java)
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }
    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Gap(height = 12.dp)
        Image(
            painter = painterResource(R.drawable.img_empty),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(112.dp)
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.CenterHorizontally)
        )
        Gap(height = 12.dp)

        HorizontalDivider()

        Gap(height = 12.dp)

        PreferenceEntry(
            title = {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = {
                onNavigate(Screens.Profile.route)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null
                )
            }
        )

        PreferenceEntry(
            title = {
                Text(
                    text = stringResource(R.string.notification),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = null
                )
            }
        )

        Gap(12.dp)
        HorizontalDivider()
        Gap(12.dp)

        PreferenceEntry(
            title = {
                Text(
                    text = stringResource(R.string.logout),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = null
                )
            },
            onClick = { showLogoutDialog = true }
        )
    }
}

