package com.example.noteweaveapp.presentation.state

import com.example.noteweaveapp.domain.model.Note

sealed class NoteDetailUiState {
    object Loading : NoteDetailUiState()
    data class Success(
        val note: Note,
        val relatedNotes: List<Note> = emptyList(),
        val aiSummary: String? = null,
        val aiSummaryState: AiProcessState = AiProcessState.Idle,
        val aiTagsState: AiProcessState = AiProcessState.Idle,
        val suggestedTags: List<String> = emptyList()
    ) : NoteDetailUiState()
    data class Error(val message: String) : NoteDetailUiState()
}

sealed class AiProcessState {
    object Idle : AiProcessState()
    object Processing : AiProcessState()
    data class Success(val result: String) : AiProcessState()
    data class Error(val message: String) : AiProcessState()
}
