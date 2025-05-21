package com.nmheir.kanicard.data.entities.option

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.entities.deck.DeckEntity

@Entity(
    tableName = "deck_configs",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"], unique = true)]
)
data class DeckConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val newCardsPerDay: Int,
    val maxReviewsCardPerDay: Int,
    val initialEase: Float,
    val graduatingIvl: Int,
    val easyIvl: Int,
    val lapseIvl: Int,
    val stepsNew: String,
    val stepsLearning: String,
    val stepsReview: String,
    val stepsRelearning: Int,
    val learningEase: Float,
    val easyBonus: Float,
    val ivlModifier: Float
)