@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.core.domain.fsrs.algorithm.FSRS
import com.nmheir.kanicard.core.domain.fsrs.algorithm.FsrsParameters
import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IDeckOptionRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.domain.repository.IReviewRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

// TODO: Tạm thời như này

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val cardRepo: ICardRepo,
    private val noteRepo: INoteRepo,
    private val deckRepo: IDeckRepo,
    private val reviewRepo: IReviewRepo,
    private val optionRepo: IDeckOptionRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId: Long = savedStateHandle["deckId"]
        ?: error("Missing deckId")

    private val parameters = optionRepo.getDeckOption(deckId)
        .map {
            FsrsParameters().copy(
                w = it.fsrsParams
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FsrsParameters())

    private val fsrs = FSRS(parameters.value)

    val startLearning = MutableStateFlow(false)

    // 1) Pure, declarative flows
    private val dueCards =
        cardRepo.getDueCardsToday(deckId)
//            .distinctUntilChanged()
            .map { it.orEmpty() }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val nIds =
        dueCards
            .map { list -> list.map { it.nId } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val noteDatas: StateFlow<List<NoteData>> =
        nIds.flatMapLatest { ids ->
            noteRepo.getNoteDataByNoteIds(ids).map { it.orEmpty() }
        }
//            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 2) Deck info
    val deck = deckRepo.getDeckDataById(deckId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val currentRecordLog = dueCards.map { cards ->
        if (cards.isEmpty()) null
        else {
            fsrs.repeat(cards.first().toFsrsCard(), OffsetDateTime.now())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val currentRatingCard = currentRecordLog.map { cards ->
        if (cards == null) emptyMap()
        else {
            Rating.entries.associateWith { r -> cards[r]!!.card }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val ratingDueInfo: StateFlow<Map<Rating, OffsetDateTime>> =
        currentRatingCard
            .map { m -> m.mapValues { it.value.due } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // 4) Combined LearningData list
    val datas = combine(noteDatas, dueCards) { notes, cards ->
        Timber.d("Note Data: %s", notes.toString())
        notes.mapNotNull { note ->
            cards.firstOrNull { it.nId == note.id }?.toFsrsCard()?.let { fsrs ->
                LearningData(note.id, fsrs, note)
            }
        }.sortedBy { it.fsrs.due }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 5) State counts
    val stateCount =
        datas.map { list ->
            val grouped = list.groupingBy { it.fsrs.state }.eachCount()
            State.entries.associateWith { grouped[it] ?: 0 }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State.entries.associateWith { 0 }
        )

    val isCompleteLearning = combine(
        startLearning, datas
    ) { isStart, datas ->
        isStart && datas.isEmpty()
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), false)

    val haveData = dueCards.map {
        it.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), null)

    // 6) Handle actions
    fun onAction(action: LearningAction) {
        when (action) {
            is LearningAction.SubmitReview -> {
                startLearning.value = true
                submitReview(action.nId, action.rating)
            }
        }
    }

    private fun submitReview(nId: Long, rating: Rating) {
        Timber.d(parameters.value.w.toString())
        viewModelScope.launch {
            val abc = dueCards.value.firstOrNull {
                it.nId == nId
            }
            if (abc == null) {
                return@launch
            }
            val a = currentRatingCard.value[rating]!!
            val b = abc.copy(
                state = a.state,
                difficulty = a.difficulty,
                due = a.due,
                stability = a.stability,
                elapsedDays = a.elapsedDays,
                scheduledDays = a.scheduledDays,
                reps = a.reps,
                lapses = a.lapses,
                lastReview = a.lastReview
            )

            cardRepo.update(b)
            reviewRepo.insert(nId, currentRecordLog.value!![rating]!!.log)
        }
    }
}

sealed interface LearningAction {
    data class SubmitReview(val nId: Long, val rating: Rating) : LearningAction
}

data class LearningData(
    val id: Long,           //note id
    val fsrs: FsrsCard,
    val noteData: NoteData
)