package com.nmheir.kanicard.data.dto

import com.squareup.moshi.Json

data class ProfileDto(
    @Json(name = "uid") val uid: String,
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)
