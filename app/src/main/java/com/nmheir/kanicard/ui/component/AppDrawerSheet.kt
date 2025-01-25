package com.nmheir.kanicard.ui.component

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.RefreshTokenKey
import com.nmheir.kanicard.ui.activities.AuthActivity
import com.nmheir.kanicard.ui.activities.MainActivity
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.startNewActivity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun AppDrawerSheet(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    client: SupabaseClient
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
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
                Gap(8.dp)
                if (isLoading) {
                    CircularProgressIndicator()
                }
            },
            buttons = {
                TextButton(
                    enabled = !isLoading,
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }

                TextButton(
                    enabled = !isLoading,
                    onClick = {
                        isLoading = true
                        scope.launch {
                            client.auth.signOut(SignOutScope.LOCAL)
                            context.dataStore.edit {
                                it.remove(RefreshTokenKey)
                            }
                            isLoading = false
                            (context as? Activity)?.startNewActivity(AuthActivity::class.java)
                        }
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

