package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.usecase.CardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    private val database: KaniDatabase,
    private val cardUseCase: CardUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

}

sealed interface DeckDetailAction {
    data class ImportDeck(val deckDto: DeckDto) : DeckDetailAction
}