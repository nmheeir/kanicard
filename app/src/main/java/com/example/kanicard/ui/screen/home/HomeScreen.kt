package com.example.kanicard.ui.screen.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.kanicard.ui.component.DeckItem
import com.example.kanicard.utils.fakeListDecks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    appBarScrollBehavior: TopAppBarScrollBehavior
) {

    BoxWithConstraints {
        val width = maxWidth

        LazyColumn(

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