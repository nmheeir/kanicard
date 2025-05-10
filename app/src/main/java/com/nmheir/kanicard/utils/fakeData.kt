package com.nmheir.kanicard.utils

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.ProfileDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.random.Random

val fakeCardList = listOf(
    CardDto(
        1,
        "What is Kotlin?",
        "A modern programming language",
        "Used for Android development",
        "2024-02-04",
        "2024-02-04",
    ),
    CardDto(
        2,
        "What is Jetpack Compose?",
        "A UI toolkit for Android",
        "Declarative UI framework",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        3,
        "What is Hilt?",
        "A dependency injection library",
        "Used in Android projects",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        4,
        "What is a sealed class?",
        "A class with restricted subclassing",
        "Used for representing state",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        5,
        "What is Coroutine?",
        "A way to write asynchronous code",
        "Lightweight threads in Kotlin",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        6,
        "What is ViewModel?",
        "An architecture component",
        "Holds UI-related data",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        7,
        "What is LiveData?",
        "An observable data holder",
        "Used in MVVM pattern",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        8,
        "What is Room?",
        "A database library",
        "Used for SQLite in Android",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        9,
        "What is Retrofit?",
        "A type-safe HTTP client",
        "Used for API calls",
        "2024-02-04",
        "2024-02-04"
    ),
    CardDto(
        10,
        "What is Flow?",
        "A reactive data stream",
        "Part of Kotlin Coroutines",
        "2024-02-04",
        "2024-02-04"
    )
)

val fakeDeckWidgetData = List(10) { index ->
    DeckWidgetData(
        deckId = index.toLong(),
        name = "Deck $index",
        reviewCount = Random.nextInt(0, 100),
        learnCount = Random.nextInt(0, 50),
        newCount = Random.nextInt(0, 30),
        dueToday = Random.nextInt(0, 20),
        lastReview = if (Random.nextBoolean()) {
            OffsetDateTime.now(ZoneOffset.UTC).minusDays(Random.nextLong(0, 30))
        } else {
            null
        }
    )
}

val fakeProfileDto = ProfileDto(
    uid = "uid",
    userName = "userName",
    email = "email",
    bio = "bio",
    avatarUrl = "https://picsum.photos/200/302"
)

val fakeFields = List(5) {
    FieldDefEntity(
        id = it.toLong(),
        name = "Field $it",
        noteTypeId = 3,
        ord = it,
        createdTime = OffsetDateTime.now(),
        modifiedTime = OffsetDateTime.now()
    )
}