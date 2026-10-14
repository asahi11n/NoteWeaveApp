package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.domain.usecase.GenerateSummaryUseCase
import com.example.noteweaveapp.domain.usecase.GenerateTagSuggestionsUseCase
import com.example.noteweaveapp.domain.usecase.GetNoteByIdUseCase
import com.example.noteweaveapp.domain.usecase.GetNotesUseCase
import com.example.noteweaveapp.domain.usecase.GetRelatedNotesUseCase
import com.example.noteweaveapp.presentation.state.AiProcessState
import com.example.noteweaveapp.presentation.state.NoteDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val getRelatedNotesUseCase: GetRelatedNotesUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val generateTagSuggestionsUseCase: GenerateTagSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = NoteDetailUiState.Loading
            try {
                val note = getNoteByIdUseCase(noteId)
                if (note != null) {
                    getNotesUseCase().collectLatest { allNotes ->
                        val relatedNotes = getRelatedNotesUseCase(noteId, allNotes)
                        _uiState.value = NoteDetailUiState.Success(
                            note = note,
                            relatedNotes = relatedNotes
                        )
                    }
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

        viewModelScope.launch {
            _uiState.value = currentState.copy(aiSummaryState = AiProcessState.Processing)
            try {
                val summary = generateSummaryUseCase(currentState.note.content)
                _uiState.value = ((_uiState.value as NoteDetailUiState.Success).copy(
                    aiSummaryState = AiProcessState.Success(summary)
                ))
            } catch (e: Exception) {
                _uiState.value = ((_uiState.value as NoteDetailUiState.Success).copy(
                    aiSummaryState = AiProcessState.Error(e.message ?: "Unknown error")
                ))
            }
        }
    }

    fun onSummaryAdopted(summary: String) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiSummary = summary,
            aiSummaryState = AiProcessState.Idle
        )
    }

    fun onSummaryRejected() {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiSummaryState = AiProcessState.Idle
        )
    }

    fun onTagsGenerate() {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(aiTagsState = AiProcessState.Processing)
            try {
                val tags = generateTagSuggestionsUseCase(
                    currentState.note.content,
                    currentState.note.tags
                )
                _uiState.value = ((_uiState.value as NoteDetailUiState.Success).copy(
                    suggestedTags = tags,
                    aiTagsState = AiProcessState.Success(tags.joinToString(", "))
                ))
            } catch (e: Exception) {
                _uiState.value = ((_uiState.value as NoteDetailUiState.Success).copy(
                    aiTagsState = AiProcessState.Error(e.message ?: "Unknown error")
                ))
            }
        }
    }

    fun onTagsAdopted(tags: List<String>) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiTagsState = AiProcessState.Idle,
            suggestedTags = emptyList()
        )
    }

    fun onTagsRejected() {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        _uiState.value = currentState.copy(
            aiTagsState = AiProcessState.Idle,
            suggestedTags = emptyList()
        )
    }
}
