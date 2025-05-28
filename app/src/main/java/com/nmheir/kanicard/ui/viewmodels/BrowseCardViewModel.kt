package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.dto.card.toCardBrowseData
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.extensions.format3
import com.nmheir.kanicard.utils.fakeCardBrowseDatas
import com.nmheir.kanicard.utils.fakeCardBrowseDtos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BrowseCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo,
    private val cardRepo: ICardRepo,
    private val deckRepo: IDeckRepo
) : ViewModel() {
    private val dId = savedStateHandle.get<Long>("deckId") ?: error("No DeckId")

    val deck = deckRepo.queryDeck(dId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val cardBrowseData = cardRepo.getBrowseCard(dId)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sortType = MutableStateFlow<SortType>(SortType.Ascending)
    val headerOption =
        MutableStateFlow<Pair<String, BrowseOption>>(Pair("Question", BrowseOption.Answer))
    val data = MutableStateFlow<List<BrowseCardUiData>>(emptyList())

    private val query = MutableStateFlow(TextFieldValue())

    @OptIn(FlowPreview::class)
    val filterData = combine(
        cardBrowseData,
        headerOption,
        sortType,
        query.debounce(300).distinctUntilChanged()
    ) { datas, option, sort, query ->
        datas?.filter {
            it.qst.contains(query.text)
        }?.toQstValuePairs(option.second, sort) ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isLoading = data
        .map {
            it.isEmpty()
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val selectedNote = data.map {
        it.filter { it.isSelect }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        combine(
            cardBrowseData,
            headerOption,
            sortType,
        ) { list, option, order ->
            list?.toQstValuePairs(option.second, order) ?: emptyList()
        }
            .distinctUntilChanged()
            .onEach {
                data.value = it
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: TextFieldValue) {
        query.value = newQuery
    }

    fun onAction(action: BrowseCardUiAction) {
        when (action) {
            is BrowseCardUiAction.ChangeBrowseOption -> {
                headerOption.update {
                    it.first to action.option
                }
            }

            is BrowseCardUiAction.SearchNote -> {
                Timber.d(action.searchValue)
            }

            is BrowseCardUiAction.ChangeSortType -> {
                sortType.value = if (sortType.value == SortType.Ascending) {
                    SortType.Descending
                } else {
                    SortType.Ascending
                }
            }

            is BrowseCardUiAction.UpdateSelect -> {
                data.update {
                    it.map {
                        if (it.nid == action.nId) {
                            it.copy(isSelect = !it.isSelect)
                        } else {
                            it
                        }
                    }
                }
            }

            BrowseCardUiAction.UpdateSelectAll -> {
                data.update {
                    it.map {
                        it.copy(isSelect = true)
                    }
                }
            }
        }
    }
}

fun List<CardBrowseData>.toQstValuePairs(
    option: BrowseOption,
    order: SortType
): List<BrowseCardUiData> {
    val comparator = when (option) {
        BrowseOption.Question -> compareBy<CardBrowseData> { it.qst }
        BrowseOption.Answer -> compareBy { it.ans }
        BrowseOption.Deck -> compareBy { it.dName }
        BrowseOption.NoteType -> compareBy { it.typeName }
        BrowseOption.Template -> compareBy { it.templateName }
        BrowseOption.Lapses -> compareBy { it.lapse }
        BrowseOption.Reviews -> compareBy { it.reviews }
        BrowseOption.Due -> compareBy { it.due }
        BrowseOption.State -> compareBy { it.state }
        BrowseOption.Modified -> compareBy { it.modifiedTime }
        BrowseOption.Created -> compareBy { it.createdTime }
    }

    val finalComparator = if (order == SortType.Descending) {
        comparator.reversed()
    } else {
        comparator
    }

    return this
        .sortedWith(finalComparator)
        .map { card ->
            val value = when (option) {
                BrowseOption.Question -> card.qst
                BrowseOption.Answer -> card.ans
                BrowseOption.Deck -> card.dName
                BrowseOption.NoteType -> card.typeName
                BrowseOption.Template -> card.templateName
                BrowseOption.Lapses -> card.lapse.toString()
                BrowseOption.Reviews -> card.reviews.toString()
                BrowseOption.Due -> card.due.format3()
                BrowseOption.State -> card.state.name
                BrowseOption.Modified -> card.modifiedTime.format3()
                BrowseOption.Created -> card.createdTime.format3()
            }
            BrowseCardUiData(
                nid = card.nid,
                qst = card.qst,
                value = value
            )
        }
}

data class BrowseCardUiData(
    val nid: Long,
    val isSelect: Boolean = false,
    val qst: String,
    val value: String,
)

sealed interface BrowseCardUiAction {
    data class ChangeBrowseOption(val option: BrowseOption) : BrowseCardUiAction
    data class SearchNote(val searchValue: String) : BrowseCardUiAction
    data class UpdateSelect(val nId: Long) : BrowseCardUiAction
    data object UpdateSelectAll : BrowseCardUiAction
    data object ChangeSortType : BrowseCardUiAction
}

enum class BrowseOption(val title: String) {
    Question("Question"),
    Answer("Answer"),
    Deck("Deck"),
    NoteType("Note type"),
    Template("Template"),
    Lapses("Lapses"),
    Reviews("Reviews"),
    Due("Due"),
    State("State"),
    Modified("Card Modified"),
    Created("Created")
}

enum class SortType {
    Ascending, Descending
}