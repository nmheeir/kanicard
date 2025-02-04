package com.nmheir.kanicard.data.dto

import com.squareup.moshi.Json

data class CardDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "question")
    val question: String,
    @Json(name = "answer")
    val answer: String,
    @Json(name = "hint")
    val hint: String,
    @Json(name = "created_at")
    val createdAt: String,
)