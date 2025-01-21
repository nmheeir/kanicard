package com.example.kanicard.ui.screen.home

import android.view.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kanicard.constants.AppBarHeight
import com.example.kanicard.ui.activities.LocalAwareWindowInset
import com.example.kanicard.ui.component.DeckItem
import com.example.kanicard.ui.component.Gap
import com.example.kanicard.utils.fakeListDecks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    appBarScrollBehavior: TopAppBarScrollBehavior
) {

    BoxWithConstraints(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        val width = maxWidth

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = LocalAwareWindowInset.current.asPaddingValues()
        ) {
            items(
                items = fakeListDecks,
                key = {
                    it.id
                }
            ) {
                DeckItem(
                    onClick = {},
                    deck = it
                )
            }
        }
    }

}