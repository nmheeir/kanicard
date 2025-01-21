package com.example.kanicard.data.entities

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String
)
