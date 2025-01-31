package com.nmheir.kanicard.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey val id: String,
    val uid: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    val bio: String,
    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String
)
