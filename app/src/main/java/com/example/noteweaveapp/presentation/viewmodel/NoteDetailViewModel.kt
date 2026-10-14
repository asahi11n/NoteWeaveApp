package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.domain.usecase.GetNoteByIdUseCase
import com.example.noteweaveapp.presentation.state.AiProcessState
import com.example.noteweaveapp.presentation.state.NoteDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = NoteDetailUiState.Loading
            try {
                val note = getNoteByIdUseCase(noteId)
                if (note != null) {
                    _uiState.value = NoteDetailUiState.Success(note = note)
                } else {
                    _uiState.value = NoteDetailUiState.Error("Note not found")
                }
            } catch (e: Exception) {
                _uiState.value = NoteDetailUiState.Error("Failed to load note: ${e.message}")
            }
        }
    }

    fun onSummaryGenerate() {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(aiSummaryState = AiProcessState.Processing)
    }

    fun onSummaryReceived(summary: String) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiSummary = summary,
            aiSummaryState = AiProcessState.Success(summary)
        )
    }

    fun onSummaryError(error: String) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiSummaryState = AiProcessState.Error(error)
        )
    }

    fun onTagsGenerate() {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(aiTagsState = AiProcessState.Processing)
    }

    fun onTagsReceived(tags: List<String>) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            suggestedTags = tags,
            aiTagsState = AiProcessState.Success(tags.joinToString(", "))
        )
    }

    fun onTagsError(error: String) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiTagsState = AiProcessState.Error(error)
        )
    }
}
