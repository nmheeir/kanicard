package com.nmheir.kanicard.utils

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.ProfileDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.ui.viewmodels.TemplatePreview
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

val fakeTemplates = List(5) {
    CardTemplateEntity(
        id = it.toLong(),
        noteTypeId = 3,
        name = "Template $it",
        qstFt = "Question $it",
        ansFt = "Answer $it",
    )
}

val fakeTemplatePreviews = listOf(
    TemplatePreview(
        id = 1L,
        name = "Simple Welcome",
        qstHtml = "Hello **{{user}}**, welcome to *{{product}}*!",
        ansHtml = "We're glad to have you here on {{date}}."
    ),
    TemplatePreview(
        id = 2L,
        name = "Feature Highlights",
        qstHtml =
            """
        Hi {{user}},

        Here's what you can expect from *{{product}}*:
        - Easy onboarding
        - Secure data
        - Great community

        Access it now: [Click here]({{url}})
    """.trimIndent(),
        ansHtml =
            """
        On {{date}}, your journey with **{{product}}** officially begins.
        We're here to help every step of the way.
    """.trimIndent()
    ),
    TemplatePreview(
        id = 3L,
        name = "Advanced Summary",
        qstHtml =
            """
        ## Summary for {{user}}

        Welcome to **{{product}}** – your start date: _{{date}}_.

        | Info      | Detail        |
        |-----------|---------------|
        | User      | {{user}}      |
        | Product   | {{product}}   |
        | Join Date | {{date}}      |
        
        {{url}}

        [More Info]({{url}})
    """.trimIndent(),
        ansHtml =
            """
        ```json
        {
          "user": "{{user}}",
          "product": "{{product}}",
          "date": "{{date}}",
          "resource": "{{url}}"
        }
        ```

        > Thank you for joining us!
    """.trimIndent()
    )

)

val fakeTemplateParams = mapOf(
    "user" to "Alice Nguyen",
    "product" to "NoteMaster Pro",
    "date" to "May 13, 2025",
    "url" to "https://google.com"
)