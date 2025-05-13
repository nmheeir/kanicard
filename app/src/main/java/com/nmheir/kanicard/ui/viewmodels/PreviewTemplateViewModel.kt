package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.repository.TemplateRepo
import com.nmheir.kanicard.extensions.md.MarkdownWithParametersParser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewTemplateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val template = savedStateHandle.get<List<TemplateState>>("template")
        ?: error("TemplateState không được truyền vào SavedStateHandle")

    val templateParse = template.map {
        it.toTemplatePreview()
    }

}

fun TemplateState.toTemplatePreview(): TemplatePreview {
    return TemplatePreview(
        id = this.id,
        name = this.name,
        qstHtml = MarkdownWithParametersParser.parseToHtml(
            this.qstState.text.toString(),
            emptyMap()
        ),
        ansHtml = MarkdownWithParametersParser.parseToHtml(
            this.ansState.text.toString(),
            emptyMap()
        )
    )
}

data class TemplatePreview(
    val id: Long,
    val name: String,
    val qstHtml: String,
    val ansHtml: String
)