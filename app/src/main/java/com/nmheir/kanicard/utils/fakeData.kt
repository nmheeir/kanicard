package com.nmheir.kanicard.utils

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.ProfileDto
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.dto.card.CardBrowseDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.data.entities.card.TemplateEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.note.FieldEntity
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartData
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartItemData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartState
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartCardData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartState
import com.nmheir.kanicard.ui.viewmodels.LearningData
import com.nmheir.kanicard.ui.viewmodels.TemplatePreview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import kotlin.math.roundToInt
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
            LocalDateTime.now(ZoneOffset.UTC).minusDays(Random.nextLong(0, 30))
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
        createdTime = LocalDateTime.now(),
        modifiedTime = LocalDateTime.now()
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
        due = LocalDateTime.parse("2025-05-16T09:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-06T08:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-06T08:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-17T10:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-07T09:30:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-08T11:15:00+07:00")
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
        due = LocalDateTime.parse("2025-05-18T11:30:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-05T07:45:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-10T12:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-19T13:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-04T10:20:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-11T14:10:00+07:00")
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
        due = LocalDateTime.parse("2025-05-20T15:45:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-03T11:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-12T16:30:00+07:00")
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
        due = LocalDateTime.parse("2025-05-21T17:20:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-02T12:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-13T18:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-22T09:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T13:30:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-14T10:15:00+07:00")
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
        due = LocalDateTime.parse("2025-05-23T14:10:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T14:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-15T15:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-24T16:30:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T15:45:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-14T17:20:00+07:00")
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
        due = LocalDateTime.parse("2025-05-25T18:50:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-02T16:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-15T18:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-16T09:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-06T08:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-06T08:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-17T10:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-07T09:30:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-08T11:15:00+07:00")
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
        due = LocalDateTime.parse("2025-05-18T11:30:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-05T07:45:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-10T12:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-19T13:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-04T10:20:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-11T14:10:00+07:00")
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
        due = LocalDateTime.parse("2025-05-20T15:45:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-03T11:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-12T16:30:00+07:00")
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
        due = LocalDateTime.parse("2025-05-21T17:20:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-02T12:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-13T18:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-22T09:00:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T13:30:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-14T10:15:00+07:00")
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
        due = LocalDateTime.parse("2025-05-23T14:10:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T14:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-15T15:00:00+07:00")
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
        due = LocalDateTime.parse("2025-05-24T16:30:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-01T15:45:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-14T17:20:00+07:00")
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
        due = LocalDateTime.parse("2025-05-25T18:50:00+07:00"),
        createdTime = LocalDateTime.parse("2025-05-02T16:00:00+07:00"),
        modifiedTime = LocalDateTime.parse("2025-05-15T18:00:00+07:00")
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
        due = LocalDateTime.now(ZoneOffset.UTC).plusDays(Random.nextLong(-3, 5)),
        stability = Random.nextDouble(0.1, 10.0),
        difficulty = Random.nextDouble(1.0, 10.0),
        elapsedDays = Random.nextLong(0, 100),
        scheduledDays = Random.nextLong(1, 30),
        reps = Random.nextLong(0, 50),
        lapses = Random.nextLong(0, 5),
        state = State.entries.random(),
        lastReview = if (Random.nextBoolean()) {
            LocalDateTime.now(ZoneOffset.UTC).minusDays(Random.nextLong(1, 60))
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

val fakeFutureDueData: Map<FutureDueChartState, FutureDueChartData> = mapOf(

    // 1 tháng: giả định lấy mẫu 7 ngày đầu
    FutureDueChartState.ONE_MONTH to FutureDueChartData(
        barData = mapOf(
            0 to 2,   // hôm nay có 2 thẻ đến hạn
            1 to 5,   // ngày mai 5 thẻ
            2 to 3,
            3 to 4,
            4 to 1,
            5 to 0,
            6 to 2
        ),
        lineData = mapOf(
            0 to 2,   // cumulative
            1 to 7,
            2 to 10,
            3 to 14,
            4 to 15,
            5 to 15,
            6 to 17
        ),
        average = 17.0 / 7,   // ≈2.43
        dueTomorrow = 5,          // barData[1]
        dailyLoad = (17.0 / 7).roundToInt() // =2
    ),

    // 3 tháng: thử với mẫu 7 ngày đầu, volume lớn hơn
    FutureDueChartState.THREE_MONTHS to FutureDueChartData(
        barData = mapOf(0 to 8, 1 to 12, 2 to 9, 3 to 7, 4 to 5, 5 to 4, 6 to 3),
        lineData = mapOf(0 to 8, 1 to 20, 2 to 29, 3 to 36, 4 to 41, 5 to 45, 6 to 48),
        average = 48.0 / 7,   // ≈6.86
        dueTomorrow = 12,
        dailyLoad = (48.0 / 7).roundToInt() // =7
    ),

    // 1 năm: sample 7 ngày đầu
    FutureDueChartState.ONE_YEAR to FutureDueChartData(
        barData = mapOf(0 to 15, 1 to 18, 2 to 20, 3 to 22, 4 to 17, 5 to 14, 6 to 10),
        lineData = mapOf(0 to 15, 1 to 33, 2 to 53, 3 to 75, 4 to 92, 5 to 106, 6 to 116),
        average = 116.0 / 7,  // ≈16.57
        dueTomorrow = 18,
        dailyLoad = (116.0 / 7).roundToInt() // =17
    ),

    // ALL: lấy sample 7 ngày, có thể “tất cả” tức không giới hạn ngày
    FutureDueChartState.ALL to FutureDueChartData(
        barData = mapOf(0 to 30, 1 to 25, 2 to 28, 3 to 22, 4 to 20, 5 to 18, 6 to 15),
        lineData = mapOf(0 to 30, 1 to 55, 2 to 83, 3 to 105, 4 to 125, 5 to 143, 6 to 158),
        average = 158.0 / 7,  // ≈22.57
        dueTomorrow = 25,
        dailyLoad = (158.0 / 7).roundToInt() // =23
    )
)

const val fakeYear = 2025
val random = Random(123)

val fakeCalendarData = CalendarChartData(
    data = (1..12).associateWith { month ->
        val daysInMonth = YearMonth.of(fakeYear, month).lengthOfMonth()
        (1..daysInMonth).map { day ->
            // Chỉ “random” với ~20% ngày có review
            val count = if (random.nextFloat() < 0.8f) {
                random.nextInt(0, 200)  // 1..5 reviews
            } else 0
            CalendarChartItemData(
                day = LocalDate.of(fakeYear, month, day),
                reviewCount = count
            )
        }
    }
)

private fun generateMockReviewChartData(
    state: ReviewChartState,
    seed: Int = 42
): ReviewChartData {
    val today = LocalDate.now()
    val days = when (state) {
        ReviewChartState.LAST_7_DAYS -> 7
        ReviewChartState.LAST_30_DAYS -> 30
        ReviewChartState.LAST_90_DAYS -> 90
        ReviewChartState.LAST_YEAR -> 365
    }

    val rand = Random(seed)
    // 1. Sinh ngày liên tục từ today - (days-1) tới today
    val allDates = (0 until days).map { i ->
        today.minusDays((days - 1 - i).toLong())
    }

    // 2. Tạo barData: với mỗi ngày, random 0..5 cho mỗi state
    val listBars = allDates.map { date ->
        ReviewChartCardData(
            learning = rand.nextInt(0, 6),
            relearning = rand.nextInt(0, 6),
            young = rand.nextInt(0, 6),
            mature = rand.nextInt(0, 6)
        )
    }

    val barData = listBars
        .mapIndexed { idx, chart -> idx to chart }
        .toMap()

    // 3. dailyTotals và lineData
    val dailyTotals = listBars.map { it.learning + it.relearning + it.young + it.mature }
    val lineData = dailyTotals
        .runningReduce { acc, v -> acc + v }
        .mapIndexed { idx, cum -> idx to cum }
        .toMap()

    // 4. Các thống kê
    val totalReviews = dailyTotals.sum()
    val daysWithReview = dailyTotals.count { it > 0 }
    val avgDayStudied = if (daysWithReview > 0) totalReviews.toDouble() / daysWithReview else 0.0
    val avgOverPeriod = totalReviews.toDouble() / days

    return ReviewChartData(
        barData = barData,
        lineData = lineData,
        dayStudied = daysWithReview,
        total = totalReviews,
        averageDayStudied = avgDayStudied,
        averageOverPeriod = avgOverPeriod
    )
}

// Ví dụ sử dụng:
val mock7 = generateMockReviewChartData(ReviewChartState.LAST_7_DAYS)

val fakeFsrsCardEntities = List(100) { index ->
    FsrsCardEntity(
        nId = index.toLong(),
        dId = 1L,
        due = LocalDateTime.now().plusDays(Random.nextLong(1, 30)),
        stability = Random.nextDouble(0.1, 20.0),
        difficulty = Random.nextDouble(1.0, 20.0), // độ khó trong khoảng 1–20
        elapsedDays = Random.nextLong(1, 100),
        scheduledDays = Random.nextLong(1, 100),
        reps = Random.nextLong(1, 20),
        lapses = Random.nextLong(0, 5),
        state = State.Review, // hoặc Random từ enum State nếu cần
        lastReview = LocalDateTime.now().minusDays(Random.nextLong(1, 30))
    )

}