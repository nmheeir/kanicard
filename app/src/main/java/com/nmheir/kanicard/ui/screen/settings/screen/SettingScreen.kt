package com.nmheir.kanicard.ui.screen.settings.screen

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.screen.Screens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.setting))
                },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding
        ) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.hashCode() }
            ) { index, item ->
                val contentColor = LocalContentColor.current
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    TextPreferenceWidget(
                        modifier = Modifier,
                        title = stringResource(item.titleRes),
                        subtitle = item.formatSubtitle(),
                        icon = item.icon,
                        onPreferenceClick = { navController.navigate(item.screen) },
                    )
                }
            }
        }
    }


}

private data class Item(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int? = null,
    val formatSubtitle: @Composable () -> String? = { subtitleRes?.let { stringResource(it) } },
    @DrawableRes val icon: Int,
    val screen: String,
)

private val items = listOf(
    Item(
        titleRes = R.string.pref_category_appearance,
        subtitleRes = R.string.pref_appearance_summary,
        icon = R.drawable.ic_palette,
        screen = "settings/appearance"
    ),
    Item(
        titleRes = R.string.pref_category_security,
        subtitleRes = R.string.pref_security_summary,
        icon = R.drawable.ic_lock,
        screen = "settings/security"
    ),
    Item(
        titleRes = R.string.pref_category_advanced,
        subtitleRes = R.string.pref_advanced_summary,
        icon = R.drawable.ic_code,
        screen = "settings/advanced"
    ),
    Item(
        titleRes = R.string.pref_category_about,
        icon = R.drawable.ic_info,
        screen = "settings/about"
    )
)