package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.repository.TemplateRepo
import com.nmheir.kanicard.extensions.md.MarkdownWithParametersParser
import com.nmheir.kanicard.utils.fakeTemplateParams
import com.nmheir.kanicard.utils.fakeTemplatePreviews
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PreviewTemplateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val template = savedStateHandle.get<List<TemplatePreview>>("templates")
        ?: fakeTemplatePreviews

    private val abc = savedStateHandle.getStateFlow<List<TemplatePreview>>("templates", emptyList())

    val templateParse = MutableStateFlow<List<TemplatePreview>>(emptyList())

    init {
        viewModelScope.launch {
            templateParse.value = template
            abc.collect {
                Timber.d(it.toString())
            }
        }
    }
}

fun TemplateState.toTemplatePreview(): TemplatePreview {
    return TemplatePreview(
        id = this.id,
        name = this.name,
        qstHtml = MarkdownWithParametersParser.parseToHtml(
            this.qstState.text.toString(),
            fakeTemplateParams
        ),
        ansHtml = MarkdownWithParametersParser.parseToHtml(
            this.ansState.text.toString(),
            fakeTemplateParams
        )
    )
}

fun TemplatePreview.toTemplateState() : TemplateState {
    return TemplateState(
        id = this.id,
        name = this.name,
        qstState = TextFieldState(initialText = this.qstHtml),
        ansState = TextFieldState(initialText = this.ansHtml)
    )
}

data class TemplatePreview(
    val id: Long,
    val name: String,
    val qstHtml: String,
    val ansHtml: String
)