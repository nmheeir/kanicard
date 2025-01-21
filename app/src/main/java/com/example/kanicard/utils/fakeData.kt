package com.example.kanicard.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kanicard.data.entities.DeckEntity
import java.time.LocalDateTime

val fakeDeck = DeckEntity(
    id = 1,
    userId = 101,
    title = "Kotlin Basics",
    thumbnail = "https://picsum.photos/200",
    description = "Learn the basics of Kotlin programming language. Perfect for beginners.",
    isPublic = true,
    createdAt = LocalDateTime.of(2023, 12, 5, 14, 30, 0, 0)
)

val fakeListDecks = listOf(
    DeckEntity(
        id = 1,
        userId = 101,
        title = "Kotlin Basics",
        thumbnail = "https://picsum.photos/200",
        description = "Learn the basics of Kotlin programming language. Perfect for beginners.",
        isPublic = true,
        createdAt = LocalDateTime.of(2023, 12, 5, 14, 30, 0, 0)
    ),
    DeckEntity(
        id = 2,
        userId = 102,
        title = "Advanced Android Development",
        thumbnail = "https://picsum.photos/200",
        description = "Dive deep into Android development with Kotlin, covering advanced topics.",
        isPublic = false,
        createdAt = LocalDateTime.of(2023, 12, 10, 10, 0, 0, 0)
    ),
    DeckEntity(
        id = 3,
        userId = 103,
        title = "JavaScript for Beginners",
        thumbnail = "https://picsum.photos/200",
        description = "Get started with JavaScript programming for web development.",
        isPublic = true,
        createdAt = LocalDateTime.of(2023, 11, 25, 9, 15, 0, 0)
    ),
    DeckEntity(
        id = 4,
        userId = 104,
        title = "React Native Crash Course",
        thumbnail = "https://picsum.photos/200",
        description = "A comprehensive crash course on React Native to build mobile apps.",
        isPublic = true,
        createdAt = LocalDateTime.of(2023, 12, 15, 16, 45, 0, 0)
    ),
    DeckEntity(
        id = 5,
        userId = 105,
        title = "Machine Learning Concepts",
        thumbnail = "https://picsum.photos/200",
        description = "Understand the fundamentals of machine learning and AI concepts.",
        isPublic = false,
        createdAt = LocalDateTime.of(2023, 12, 20, 18, 30, 0, 0)
    )
)
