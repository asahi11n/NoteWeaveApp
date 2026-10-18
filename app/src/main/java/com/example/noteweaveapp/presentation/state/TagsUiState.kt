package com.example.noteweaveapp.presentation.state

import com.example.noteweaveapp.util.tags.TagUsage

data class TagsUiState(
    val usages: List<TagUsage> = emptyList(),
    val createError: UiError? = null
)

sealed interface TagsUiEvent {
    data object TagCreated : TagsUiEvent
}
