package com.nmheir.kanicard.ui.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.screen.Screens

@Composable
fun AppDrawerSheet(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
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

        HorizontalDivider(
            thickness = 2.dp
        )

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
    }
}

