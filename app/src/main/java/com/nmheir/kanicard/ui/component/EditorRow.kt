package com.nmheir.kanicard.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.ui.theme.KaniTheme

@Composable
fun MarkdownEditorRow(
    canUndo: Boolean,
    canRedo: Boolean,
    onEdit: (String) -> Unit,
    onListButtonClick: () -> Unit,
    onInsertFieldButtonClick: () -> Unit
) {
    var showHeadingLevel by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomAppBarDefaults.containerColor)
            .navigationBarsPadding()
            .height(48.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        IconButtonTooltip(
            enabled = canUndo,
            iconRes = R.drawable.ic_undo,
            shortCutDescription = "Undo"
        ) {
            onEdit(Constants.Editor.UNDO)
        }

        IconButtonTooltip(
            enabled = canRedo,
            iconRes = R.drawable.ic_redo,
            shortCutDescription = "Redo"
        ) {
            onEdit(Constants.Editor.REDO)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_insert_text,
            shortCutDescription = "Insert field"
        ) {
            onInsertFieldButtonClick()
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_title,
            shortCutDescription = "Heading Level"
        ) {
            showHeadingLevel = !showHeadingLevel
        }

        AnimatedVisibility(
            visible = showHeadingLevel
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButtonTooltip(
                    iconRes = R.drawable.format_h1,
                    shortCutDescription = "Heading 1"
                ) {
                    onEdit(Constants.Editor.H1)
                }

                IconButtonTooltip(
                    iconRes = R.drawable.format_h2,
                    shortCutDescription = "Heading 2"
                ) {
                    onEdit(Constants.Editor.H2)
                }

                IconButtonTooltip(
                    iconRes = R.drawable.format_h3,
                    shortCutDescription = "Heading 3"
                ) {
                    onEdit(Constants.Editor.H3)
                }

                IconButtonTooltip(
                    iconRes = R.drawable.format_h4,
                    shortCutDescription = "Heading 4"
                ) {
                    onEdit(Constants.Editor.H4)
                }

                IconButtonTooltip(
                    iconRes = R.drawable.format_h5,
                    shortCutDescription = "Heading 5"
                ) {
                    onEdit(Constants.Editor.H5)
                }

                IconButtonTooltip(
                    iconRes = R.drawable.format_h6,
                    shortCutDescription = "Heading 6"
                ) {
                    onEdit(Constants.Editor.H6)
                }
            }
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_format_bold,
            shortCutDescription = "Bold"
        ) {
            onEdit(Constants.Editor.BOLD)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_format_italic,
            shortCutDescription = "Italic"
        ) {
            onEdit(Constants.Editor.ITALIC)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_format_underline,
            shortCutDescription = "Underline"
        ) {
            onEdit(Constants.Editor.UNDERLINE)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_format_indent_increase,
            shortCutDescription = "Tab"
        ) {
            onEdit(Constants.Editor.TAB)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_format_indent_decrease,
            shortCutDescription = "UnTab"
        ) {
            onEdit(Constants.Editor.UN_TAB)
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_list,
            shortCutDescription = "Ctrl + Shift + L"
        ) {
            onListButtonClick()
        }

        IconButtonTooltip(
            iconRes = R.drawable.ic_text_snippet,
            shortCutDescription = "Templates"
        ) {
            onEdit(Constants.Editor.TEXT)
        }
    }
}