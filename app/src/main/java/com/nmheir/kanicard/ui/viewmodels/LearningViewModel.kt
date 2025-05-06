package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.core.domain.fsrs.algorithm.FSRS
import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import javax.inject.Inject

// TODO: Tạm thời như này

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val database: KaniDatabase,
    private val savedStatedHandle: SavedStateHandle
) : ViewModel() {

    val deckId = savedStatedHandle.get<Long>("deckId")

    private val _channel = Channel<LearningEvent>()
    val channel = _channel.receiveAsFlow()

    val isLoading = MutableStateFlow(false)

    //danh sách thẻ trong deck
//    private val cards = MutableStateFlow<List<DownloadedCardEntity>?>(null)

    //danh sách thẻ cần học
    private val fsrsCards = MutableStateFlow<List<FsrsCardEntity>?>(null)

    //Danh sách thẻ đã đến hạn học
    private val dueFsrsCards = MutableStateFlow<List<FsrsCardEntity>?>(null)

//    fun onAction(action: LearningAction) {
//        when (action) {
//            is LearningAction.SubmitReview -> submitReview(action.fsrsCard, action.rating)
//        }
//    }

    init {
        isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when {
                    deckId == null -> return@withContext
                    else -> {
                        //Lấy danh sách thẻ trong deck
//                        cards.value = database.getDownloadedCardByDeckId(deckId).first()
                        //Lấy danh sách thẻ cần học
                        fsrsCards.value = database.getFsrsCardByDeckId(deckId).first()

                        //If user never learn this deck before
                        if (fsrsCards.value.isNullOrEmpty()) {
                            initialCardIntoFsrs()
                        }
                        //fetch fsrsCard need to learn
                        filterFsrsCardToLearn()
                    }
                }
            }
            isLoading.value = false
        }
    }

    /** Lọc thẻ cần học theo biến due trong fsrsCardEntity.
     *
     * Nếu due <= now -> Thẻ cần học.
     *
     * due > now -> Thẻ chưa cần học.
     * */
    private suspend fun filterFsrsCardToLearn() {
        //Lọc
        val now = OffsetDateTime.now()
        dueFsrsCards.value = fsrsCards.value?.filter {
            it.due.isBefore(now)
        }

        //Case 1: Nếu không có thẻ cần học
        if (dueFsrsCards.value.isNullOrEmpty()) {
            _channel.send(LearningEvent.NoCardToLearn)
            return
        }

        //Case 2: Nếu có thẻ cần học
        _channel.send(LearningEvent.HaveCardToLearn)
    }

    /**
     *If in table fsrs_card doesn't have any card -> That means user never learn this deck before
     **/
    private suspend fun initialCardIntoFsrs() {
        coroutineScope {
//            cards.value?.fastForEach {
//                val fsrsCardEntity = FsrsCardEntity.createEmpty(
//                    cardId = it.id,
//                    deckId = it.deckId,
//                    now = OffsetDateTime.now()
//                )
//
//                withContext(Dispatchers.IO) {
//                    database.insert(fsrsCardEntity)
//                    Timber.d("Initial card into fsrs: ${fsrsCardEntity.cardId}")
//                }
//            }
        }
    }

    private fun submitReview(fsrsCard: FsrsCard, rating: Rating, cardId: Long, deckId: Long) {
        viewModelScope.launch {
            val recordLog = FSRS().repeat(fsrsCard, OffsetDateTime.now())
            val fsrsCard = recordLog[rating]?.card!!
            val reviewLog = recordLog[rating]?.log!!

            val fsrsCardEntity = FsrsCardEntity(
                id = cardId,
                deckId = deckId,
                due = fsrsCard.due,
                stability = fsrsCard.stability,
                difficulty = fsrsCard.difficulty,
                elapsedDays = fsrsCard.elapsedDays,
                scheduledDays = fsrsCard.scheduledDays,
                reps = fsrsCard.reps,
                lapses = fsrsCard.lapses,
                state = fsrsCard.state,
                lastReview = reviewLog.review,
                nId = 0
            )

            //thêm vào db
            withContext(Dispatchers.IO) {
                database.insert(fsrsCardEntity)
            }
        }
    }
}

sealed interface LearningEvent {
    data object UserNeverLearnBefore : LearningEvent
    data object NoCardToLearn : LearningEvent
    data object HaveCardToLearn : LearningEvent
}

sealed interface LearningAction {
    data class SubmitReview(val fsrsCard: FsrsCard, val rating: Rating) : LearningAction
}