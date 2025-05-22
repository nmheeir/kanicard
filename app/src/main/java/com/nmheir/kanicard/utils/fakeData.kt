package com.nmheir.kanicard.utils

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.ProfileDto
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.dto.card.CardBrowseDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.data.entities.card.TemplateEntity
import com.nmheir.kanicard.data.entities.note.FieldEntity
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.ui.viewmodels.LearningData
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
        optionId = index.toLong(),
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
    FieldEntity(
        id = it.toLong(),
        name = "Field $it",
        ntId = 3,
        ord = it,
        createdTime = OffsetDateTime.now(),
        modifiedTime = OffsetDateTime.now()
    )
}

val fakeTemplates = List(5) {
    TemplateEntity(
        id = it.toLong(),
        ntId = 3,
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

val fakeNoteData = List(10) {
    NoteData(
        id = it.toLong(),
        dId = 0L,
        qHtml = "Question $it",
        aHtml = "Answer $it",
    )
}

val fakeCardBrowseDatas = listOf(
    CardBrowseData(
        nid = 201L,
        tId = 10L,
        dName = "Deck Basics",
        typeName = "Type Simple",
        templateName = "BasicQA",
        qst = "Thủ đô Nhật Bản",
        ans = "Tokyo",
        lapse = 0L,
        state = State.New,
        reviews = 0,
        due = OffsetDateTime.parse("2025-05-16T09:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-06T08:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-06T08:00:00+07:00")
    ),
    // 2. With hint
    CardBrowseData(
        nid = 202L,
        tId = 11L,
        dName = "Deck Geography",
        typeName = "Type Hinted",
        templateName = "GeoHint",
        qst = "Sông Nile, Châu Phi",
        ans = "Nile",
        lapse = 1L,
        state = State.Learning,
        reviews = 2,
        due = OffsetDateTime.parse("2025-05-17T10:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-07T09:30:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-08T11:15:00+07:00")
    ),
    // 3. Meta category
    CardBrowseData(
        nid = 203L,
        tId = 12L,
        dName = "Deck History",
        typeName = "Type Meta",
        templateName = "HistMeta",
        qst = "[Sử] Năm kết thúc WW2",
        ans = "1945",
        lapse = 0L,
        state = State.Review,
        reviews = 5,
        due = OffsetDateTime.parse("2025-05-18T11:30:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-05T07:45:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-10T12:00:00+07:00")
    ),
    // 4. Multiple tags
    CardBrowseData(
        nid = 204L,
        tId = 13L,
        dName = "Deck Science",
        typeName = "Type Tags",
        templateName = "SciTags",
        qst = "Định luật Ôm, vật lý",
        ans = "V = I × R",
        lapse = 2L,
        state = State.Relearning,
        reviews = 8,
        due = OffsetDateTime.parse("2025-05-19T13:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-04T10:20:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-11T14:10:00+07:00")
    ),
    // 5. Notes included
    CardBrowseData(
        nid = 205L,
        tId = 14L,
        dName = "Deck Math",
        typeName = "Type Notes",
        templateName = "MathNote",
        qst = "Tích phân, nhớ lũy thừa",
        ans = "∫ x^n dx = x^{n+1}/(n+1)",
        lapse = 1L,
        state = State.Relearning,
        reviews = 10,
        due = OffsetDateTime.parse("2025-05-20T15:45:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-03T11:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-12T16:30:00+07:00")
    ),
    // 6. Complex meta and hint
    CardBrowseData(
        nid = 206L,
        tId = 15L,
        dName = "Deck Advanced",
        typeName = "Type Full",
        templateName = "FullMeta",
        qst = "Định lý Pythagoras, hình học",
        ans = "a² + b² = c²",
        lapse = 3L,
        state = State.Review,
        reviews = 12,
        due = OffsetDateTime.parse("2025-05-21T17:20:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-02T12:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-13T18:00:00+07:00")
    ),
    // 7. Missing hint
    CardBrowseData(
        nid = 207L,
        tId = 16L,
        dName = "Deck General",
        typeName = "Type One",
        templateName = "OneField",
        qst = "Kí hiệu hóa học của vàng",
        ans = "Au",
        lapse = 0L,
        state = State.New,
        reviews = 1,
        due = OffsetDateTime.parse("2025-05-22T09:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T13:30:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-14T10:15:00+07:00")
    ),
    // 8. Multiple meta fields
    CardBrowseData(
        nid = 208L,
        tId = 17L,
        dName = "Deck Languages",
        typeName = "Type Multi",
        templateName = "LangMeta",
        qst = "[N5] Xin chào, Nhật Bản",
        ans = "こんにちは",
        lapse = 2L,
        state = State.Relearning,
        reviews = 15,
        due = OffsetDateTime.parse("2025-05-23T14:10:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T14:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-15T15:00:00+07:00")
    ),
    // 9. Long hint text
    CardBrowseData(
        nid = 209L,
        tId = 18L,
        dName = "Deck Literature",
        typeName = "Type Hint",
        templateName = "LitHint",
        qst = "Nhân vật chính, Gatsby",
        ans = "Jay Gatsby",
        lapse = 1L,
        state = State.Learning,
        reviews = 7,
        due = OffsetDateTime.parse("2025-05-24T16:30:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T15:45:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-14T17:20:00+07:00")
    ),
    // 10. Full complexity
    CardBrowseData(
        nid = 210L,
        tId = 19L,
        dName = "Deck Universe",
        typeName = "Type All",
        templateName = "UniverseQA",
        qst = "Hệ Mặt Trời, khoảng cách: 149.6 triệu km",
        ans = "Trái Đất",
        lapse = 3L,
        state = State.Relearning,
        reviews = 20,
        due = OffsetDateTime.parse("2025-05-25T18:50:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-02T16:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-15T18:00:00+07:00")
    )
)

val fakeCardBrowseDtos = listOf(
// 1
    CardBrowseDto(
        nid = 201L,
        tId = 10L,
        dName = "Deck Basics",
        typeName = "Type Simple",
        templateName = "BasicQA",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Thủ đô Nhật Bản","back":"Tokyo"}""",
        lapse = 0L,
        state = State.New,
        reviews = 0,
        due = OffsetDateTime.parse("2025-05-16T09:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-06T08:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-06T08:00:00+07:00")
    ),
    // 2
    CardBrowseDto(
        nid = 202L,
        tId = 11L,
        dName = "Deck Geography",
        typeName = "Type Hinted",
        templateName = "GeoHint",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Sông Nile, Châu Phi","back":"Nile"}""",
        lapse = 1L,
        state = State.Learning,
        reviews = 2,
        due = OffsetDateTime.parse("2025-05-17T10:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-07T09:30:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-08T11:15:00+07:00")
    ),
    // 3
    CardBrowseDto(
        nid = 203L,
        tId = 12L,
        dName = "Deck History",
        typeName = "Type Meta",
        templateName = "HistMeta",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"[Sử] Năm kết thúc WW2","back":"1945"}""",
        lapse = 0L,
        state = State.Review,
        reviews = 5,
        due = OffsetDateTime.parse("2025-05-18T11:30:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-05T07:45:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-10T12:00:00+07:00")
    ),
    // 4
    CardBrowseDto(
        nid = 204L,
        tId = 13L,
        dName = "Deck Science",
        typeName = "Type Tags",
        templateName = "SciTags",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Định luật Ôm, vật lý","back":"V = I × R"}""",
        lapse = 2L,
        state = State.Relearning,
        reviews = 8,
        due = OffsetDateTime.parse("2025-05-19T13:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-04T10:20:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-11T14:10:00+07:00")
    ),
    // 5
    CardBrowseDto(
        nid = 205L,
        tId = 14L,
        dName = "Deck Math",
        typeName = "Type Notes",
        templateName = "MathNote",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Tích phân, nhớ lũy thừa","back":"∫ x^n dx = x^{n+1}/(n+1)"}""",
        lapse = 1L,
        state = State.Relearning,
        reviews = 10,
        due = OffsetDateTime.parse("2025-05-20T15:45:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-03T11:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-12T16:30:00+07:00")
    ),
    // 6
    CardBrowseDto(
        nid = 206L,
        tId = 15L,
        dName = "Deck Advanced",
        typeName = "Type Full",
        templateName = "FullMeta",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Định lý Pythagoras, hình học","back":"a² + b² = c²"}""",
        lapse = 3L,
        state = State.Review,
        reviews = 12,
        due = OffsetDateTime.parse("2025-05-21T17:20:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-02T12:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-13T18:00:00+07:00")
    ),
    // 7
    CardBrowseDto(
        nid = 207L,
        tId = 16L,
        dName = "Deck General",
        typeName = "Type One",
        templateName = "OneField",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Kí hiệu hóa học của vàng","back":"Au"}""",
        lapse = 0L,
        state = State.New,
        reviews = 1,
        due = OffsetDateTime.parse("2025-05-22T09:00:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T13:30:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-14T10:15:00+07:00")
    ),
    // 8
    CardBrowseDto(
        nid = 208L,
        tId = 17L,
        dName = "Deck Languages",
        typeName = "Type Multi",
        templateName = "LangMeta",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"[N5] Xin chào, Nhật Bản","back":"こんにちは"}""",
        lapse = 2L,
        state = State.Relearning,
        reviews = 15,
        due = OffsetDateTime.parse("2025-05-23T14:10:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T14:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-15T15:00:00+07:00")
    ),
    // 9
    CardBrowseDto(
        nid = 209L,
        tId = 18L,
        dName = "Deck Literature",
        typeName = "Type Hint",
        templateName = "LitHint",
        qfmt = "{{front}}",
        afmt = "{{back}}",
        field = """{"front":"Nhân vật chính, Gatsby","back":"Jay Gatsby"}""",
        lapse = 1L,
        state = State.Learning,
        reviews = 7,
        due = OffsetDateTime.parse("2025-05-24T16:30:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-01T15:45:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-14T17:20:00+07:00")
    ),
    // 10
    CardBrowseDto(
        nid = 210L,
        tId = 19L,
        dName = "Deck Universe",
        typeName = "Type All",
        templateName = "UniverseQA",
        qfmt = "{{front}} {{back}}",
        afmt = "{{back}}",
        field = """{"front":"Hệ Mặt Trời, khoảng cách: 149.6 triệu km","back":"Trái Đất"}""",
        lapse = 3L,
        state = State.Relearning,
        reviews = 20,
        due = OffsetDateTime.parse("2025-05-25T18:50:00+07:00"),
        createdTime = OffsetDateTime.parse("2025-05-02T16:00:00+07:00"),
        modifiedTime = OffsetDateTime.parse("2025-05-15T18:00:00+07:00")
    )
)

val fakeStateCount = mapOf(
    State.New to 100,
    State.Learning to 1,
    State.Relearning to 5,
    State.Review to 20
)

val fakeFsrsCards = List(10) {
    FsrsCard(
        due = OffsetDateTime.now(ZoneOffset.UTC).plusDays(Random.nextLong(-3, 5)),
        stability = Random.nextDouble(0.1, 10.0),
        difficulty = Random.nextDouble(1.0, 10.0),
        elapsedDays = Random.nextLong(0, 100),
        scheduledDays = Random.nextLong(1, 30),
        reps = Random.nextLong(0, 50),
        lapses = Random.nextLong(0, 5),
        state = State.entries.random(),
        lastReview = if (Random.nextBoolean()) {
            OffsetDateTime.now(ZoneOffset.UTC).minusDays(Random.nextLong(1, 60))
        } else {
            null
        }
    )
}

val fakeNoteDatas = List(10) { index ->
    NoteData(
        id = index + 1L,
        dId = (index % 3 + 1).toLong(), // Giả định có 3 deck khác nhau
        qHtml = "<p><b>Question ${index + 1}:</b> What is ${index + 1} + ${index + 2}?</p>",
        aHtml = "<p><i>Answer:</i> ${index + 1 + index + 2}</p>"
    )
}

val fakeLearningDataList = List(10) { index ->
    LearningData(
        id = fakeNoteDatas[index].id,
        fsrs = fakeFsrsCards[index],
        noteData = fakeNoteDatas[index]
    )
}
