package com.nmheir.kanicard.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserProfile(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "uid"
    )
    val profile: Profile
)