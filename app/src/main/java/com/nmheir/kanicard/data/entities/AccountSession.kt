package com.nmheir.kanicard.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_sessions")
data class AccountSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String
)
