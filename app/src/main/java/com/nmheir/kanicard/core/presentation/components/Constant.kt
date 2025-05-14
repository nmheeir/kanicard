package com.nmheir.kanicard.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

val topSmallPaddingValues = PaddingValues(top = MaterialTheme.padding.small)

const val DISABLED_ALPHA = .38f
const val SECONDARY_ALPHA = .78f

class Padding {

    val extraLarge = 32.dp

    val large = 24.dp

    val mediumLarge = 28.dp

    val medium = 16.dp

    val mediumSmall = 12.dp

    val small = 8.dp

    val extraSmall = 4.dp
}

val MaterialTheme.padding: Padding
    get() = Padding()

object Constants {
    object File {
        const val KANI_CARD = "KaniCard"
        const val KANI_CARD_BACKUP = "Backup"
        const val KANI_CARD_IMAGE = "Images"
        const val KANI_CARD_AUDIO = "Audios"
        const val KANI_CARD_VIDEO = "Videos"
    }

    object Editor {
        const val UNDO = "undo"
        const val REDO = "redo"

        const val INSERT_FIELD = "insert_field"

        const val H1 = "h1"
        const val H2 = "h2"
        const val H3 = "h3"
        const val H4 = "h4"
        const val H5 = "h5"
        const val H6 = "h6"

        const val BOLD = "bold"
        const val ITALIC = "italic"
        const val UNDERLINE = "underline"
        const val STRIKETHROUGH = "strikethrough"
        const val MARK = "mark"

        const val INLINE_CODE = "inlineCode"
        const val INLINE_BRACKETS = "inlineBrackets"
        const val INLINE_BRACES = "inlineBraces"
        const val INLINE_MATH = "inlineMath"

        const val TABLE = "table"
        const val TASK = "task"
        const val LIST = "list"
        const val QUOTE = "quote"
        const val TAB = "tab"
        const val UN_TAB = "unTab"
        const val RULE = "rule"
        const val DIAGRAM = "diagram"

        const val TEXT = "text"
    }
}
