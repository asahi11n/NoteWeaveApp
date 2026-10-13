package com.example.noteweaveapp.presentation.state

import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag

data class NoteListUiState(
    val notes: List<Note> = emptyList(),
    val allTags: List<Tag> = emptyList(),
    val selectedTagIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.UPDATED_DESC,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SortOption {
    UPDATED_DESC,
    UPDATED_ASC,
    TITLE_ASC
}
