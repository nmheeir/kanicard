package com.nmheir.kanicard.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.screen.Screens

@Composable
fun DrawerContent(
    onNavigate: (Screens) -> Unit
) {
    ModalDrawerSheet {
        Text("Drawer title", modifier = Modifier.padding(16.dp))

        HorizontalDivider()

        DrawerItem(
            label = stringResource(R.string.setting),
            iconRes = R.drawable.ic_setting,
            onClick = { onNavigate(Screens.SettingsScreen.Setting) }
        )

        DrawerItem(
            label = "Deck Options",
            iconRes = R.drawable.ic_lists,
            onClick = {}
        )

        HorizontalDivider()

        Text(
            text = "Version abc xyz...."
        )
    }
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    label: String,
    @DrawableRes iconRes: Int,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        selected = false,
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = iconTint
            )
        },
        onClick = onClick,
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}