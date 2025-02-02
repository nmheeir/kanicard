package com.nmheir.kanicard.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Profile(
    @PrimaryKey @Json(name = "uid") val uid: String,
    @ColumnInfo(name = "user_name")
    @Json(name = "user_name") val userName: String? = null,
    @ColumnInfo(name = "email")
    @Json(name = "email") val email: String? = null,
    @ColumnInfo(name = "bio")
    @Json(name = "bio") val bio: String? = null,
    @ColumnInfo(name = "avatar_url")
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "updated_at")
    @Json(name = "updated_at") val updatedAt: String? = null
)
