package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BrowseCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo
) : ViewModel() {
    private val dId = savedStateHandle.get<Long>("deckId") ?: error("No DeckId")

    val deck = MutableStateFlow<DeckDto?>(null)

}