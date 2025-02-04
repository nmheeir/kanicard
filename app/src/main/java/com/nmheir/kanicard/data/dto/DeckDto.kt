package com.nmheir.kanicard.data.dto

import com.squareup.moshi.Json

data class DeckDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "creator")
    val creator: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "thumbnail")
    val thumbnail: String,
    @Json(name = "created_at")
    val createdAt: String
)