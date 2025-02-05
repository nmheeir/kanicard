package com.nmheir.kanicard.data.dto

import com.squareup.moshi.Json

data class DeckDetailDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "creator")
    val creator: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "thumbnail")
    val thumbnail: String ?= null,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "last_updated")
    val lastUpdated: String,
    @Json(name = "profiles")
    val profileDto: ProfileDto,
) {
    fun toDeck(): DeckDto = DeckDto(
        id = id,
        creator = creator,
        title = title,
        description = description,
        thumbnail = thumbnail,
        createdAt = createdAt,
        lastUpdated = lastUpdated
    )

    fun toProfile(): ProfileDto = profileDto
}
