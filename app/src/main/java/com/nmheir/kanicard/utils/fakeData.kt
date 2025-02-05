package com.nmheir.kanicard.utils

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.dto.ProfileDto

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

val fakeDeckDto = DeckDto(
    id = 1,
    creator = "nmheir",
    title = "What is Kotlin?",
    description = "Lorem ipsum odor amet, consectetuer adipiscing elit. Per aliquet metus massa amet ultricies malesuada primis. Imperdiet nunc eros quisque magna erat sapien cras montes egestas. Sociosqu metus metus primis laoreet; elit curabitur. Penatibus adipiscing sodales interdum lobortis orci penatibus augue imperdiet. Ipsum dictumst quam ridiculus, habitant interdum primis nam convallis. Quisque erat vivamus vitae eros habitasse consequat torquent. Pellentesque condimentum vehicula dui rutrum amet orci orci. Imperdiet mus aptent sed dis erat.\n" +
            "\n" +
            "Elit mauris ante fringilla est habitant; netus per leo malesuada. Vulputate nam mus himenaeos maecenas duis, phasellus sollicitudin pellentesque erat. Magna eros non fermentum commodo cubilia dictum adipiscing sollicitudin netus. Odio placerat natoque cubilia urna semper convallis porta netus. Vulputate lacus mi est purus donec orci libero. Odio volutpat sapien malesuada aenean, nostra imperdiet maecenas.",
    thumbnail = "https://picsum.photos/200/301",
    createdAt = "2024-02-04",
    lastUpdated = "2024-02-04",
    totalCard = 10,
    isPublic = true
)

val fakeProfileDto = ProfileDto(
    uid = "uid",
    userName = "userName",
    email = "email",
    bio = "bio",
    avatarUrl = "https://picsum.photos/200/302"
)