package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.dto.card.toCardBrowseData
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.utils.fakeCardBrowseDatas
import com.nmheir.kanicard.utils.fakeCardBrowseDtos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BrowseCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo,
    private val cardRepo: ICardRepo
) : ViewModel() {
    private val dId = savedStateHandle.get<Long>("deckId") ?: error("No DeckId")

    val deck = MutableStateFlow<DeckDto?>(null)

    val isLoading = MutableStateFlow(false)

    val cardBrowseData = MutableStateFlow<List<CardBrowseData>>(fakeCardBrowseDatas)

    init {
        cardRepo.getBrowseCard(dId)
            .distinctUntilChanged()
            .onEach {
                Timber.d("cardBrowseData: $it")
//                cardBrowseData.value = it ?: fakeCardBrowseDatas
                cardBrowseData.value = fakeCardBrowseDtos.map {
                    it.toCardBrowseData()
                }
            }
            .launchIn(viewModelScope)
    }

}