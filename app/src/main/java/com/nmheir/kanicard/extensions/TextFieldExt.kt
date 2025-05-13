package com.nmheir.kanicard.extensions

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.TextRange

private fun TextFieldBuffer.inlineWrap(
    startWrappedString: String,
    endWrappedString: String = startWrappedString,
    initialSelection: TextRange = selection
) = if (initialSelection.collapsed) {
    // No text selected, insert at cursor position and place cursor in the middle
    replace(initialSelection.min, initialSelection.min, startWrappedString + endWrappedString)
    selection = TextRange(
        initialSelection.min + startWrappedString.length,
        initialSelection.min + startWrappedString.length
    )
} else {
    replace(initialSelection.min, initialSelection.min, startWrappedString)
    replace(
        initialSelection.max + startWrappedString.length,
        initialSelection.max + startWrappedString.length,
        endWrappedString
    )
    selection = TextRange(
        initialSelection.min,
        initialSelection.max + startWrappedString.length + endWrappedString.length
    )
}

fun TextFieldBuffer.bold() = inlineWrap("**")

fun TextFieldBuffer.italic() = inlineWrap("_")

fun TextFieldBuffer.underline() = inlineWrap("++")

fun TextFieldBuffer.strikeThrough() = inlineWrap("~~")

fun TextFieldBuffer.mark() = inlineWrap("<mark>", "</mark>")

fun TextFieldBuffer.inlineBrackets() = inlineWrap("[", "]")

fun TextFieldBuffer.inlineBraces() = inlineWrap("{", "}")

fun TextFieldBuffer.inlineCode() = inlineWrap("`")

fun TextFieldBuffer.inlineMath() = inlineWrap("$")

fun TextFieldBuffer.tab() {
    val text = toString()
    val lineStart = text.take(selection.min)
        .lastIndexOf('\n')
        .takeIf { it != -1 }
        ?.let { it + 1 }
        ?: 0

    val initialSelection = selection

    replace(lineStart, lineStart, "    ") // 4 spaces
    selection = TextRange(
        initialSelection.min + 4,
        initialSelection.max + 4
    )
}

fun TextFieldBuffer.unTab() {
    val text = toString()
    val lineStart = text.take(selection.min)
        .lastIndexOf('\n')
        .takeIf { it != -1 }
        ?.let { it + 1 }
        ?: 0

    val tabIndex = text.indexOf("    ", lineStart)
    val initialSelection = selection

    if (tabIndex != -1 && tabIndex < selection.min) {
        replace(tabIndex, tabIndex + 4, "")
        selection = TextRange(
            initialSelection.min - 4,
            initialSelection.max - 4
        )
    }
}

fun TextFieldBuffer.add(str: String) {
    val initialSelection = selection
    replace(initialSelection.min, initialSelection.max, str)
}


fun TextFieldBuffer.addInNewLine(str: String) {
    val text = toString()
    if (selection.min != 0 && text[selection.min - 1] != '\n') {
        // 如果不是换行符，那么就先添加一个换行符
        add("\n")
    }
    add(str)
}

fun TextFieldBuffer.addHeader(level: Int) {
    addInNewLine("#".repeat(level) + " ")
}
