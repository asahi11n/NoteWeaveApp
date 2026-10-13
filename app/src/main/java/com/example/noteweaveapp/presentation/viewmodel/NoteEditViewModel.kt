package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.model.Tag
import com.example.noteweaveapp.domain.usecase.CreateNoteUseCase
import com.example.noteweaveapp.domain.usecase.GetNoteByIdUseCase
import com.example.noteweaveapp.domain.usecase.UpdateNoteUseCase
import com.example.noteweaveapp.presentation.state.NoteEditUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteEditViewModel(
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditUiState())
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    init {
        loadAvailableTags()
    }

    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                val tags = tagRepository.getAllTags()
                _uiState.value = _uiState.value.copy(availableTags = tags)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load tags: ${e.message}"
                )
            }
        }
    }

    fun loadNoteForEdit(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val note = getNoteByIdUseCase(noteId)
                if (note != null) {
                    _uiState.value = _uiState.value.copy(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        tags = note.tags,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Note not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load note: ${e.message}"
                )
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onContentChanged(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun onTagAdded(tag: Tag) {
        val currentTags = _uiState.value.tags
        if (!currentTags.any { it.id == tag.id }) {
            _uiState.value = _uiState.value.copy(tags = currentTags + tag)
        }
    }

    fun onTagRemoved(tagId: Long) {
        _uiState.value = _uiState.value.copy(
            tags = _uiState.value.tags.filter { it.id != tagId }
        )
    }

    fun onSave() {
        val state = _uiState.value
        _uiState.value = state.copy(isSaving = true, saveSuccess = false)

        viewModelScope.launch {
            val result = if (state.id == 0L) {
                createNoteUseCase(
                    title = state.title,
                    content = state.content,
                    tagNames = state.tags.map { it.name }
                )
            } else {
                updateNoteUseCase(
                    noteId = state.id,
                    title = state.title,
                    content = state.content,
                    tagNames = state.tags.map { it.name }
                ).mapCatching { Unit as Unit }
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    error = null
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save note: ${e.message}"
                )
            }
        }
    }
}
