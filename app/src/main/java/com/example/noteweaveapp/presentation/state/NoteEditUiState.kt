package com.example.noteweaveapp.presentation.state

import com.example.noteweaveapp.domain.model.Tag

data class NoteEditUiState(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val tags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: UiError? = null
)
