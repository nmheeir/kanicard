package com.nmheir.kanicard.data.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@Immutable
@Entity(
    tableName = "decks"
)
data class DeckEntity(
    @Json(name = "id")
    @PrimaryKey val id: Long,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "thumbnail")
    val thumbnail: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "is_public")
    val isPublic: Boolean,
    @Json(name = "created_at")
    val createdAt: String
)
