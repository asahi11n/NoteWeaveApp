package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.usecase.DeleteNoteUseCase
import com.example.noteweaveapp.domain.usecase.GetAllTagsUseCase
import com.example.noteweaveapp.domain.usecase.GetNotesUseCase
import com.example.noteweaveapp.presentation.state.FailedAction
import com.example.noteweaveapp.presentation.state.NoteListUiState
import com.example.noteweaveapp.presentation.state.SortOption
import com.example.noteweaveapp.presentation.state.UiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    // 検索・タグ・並び替えの変更時に再計算できるよう、DB の全ノートを保持する
    private var allNotes: List<Note> = emptyList()

    init {
        // タグ画面から開いた場合は、そのタグで絞り込んだ状態から始める（-1 は指定なし）
        savedStateHandle.get<Long>("tagId")
            ?.takeIf { it >= 0L }
            ?.let { tagId -> _uiState.value = _uiState.value.copy(selectedTagIds = setOf(tagId)) }
        loadTags()
        loadNotes()
    }

    private fun loadTags() {
        viewModelScope.launch {
            getAllTagsUseCase().collectLatest { tags ->
                _uiState.value = _uiState.value.copy(allTags = tags)
            }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getNotesUseCase().collectLatest { notes ->
                    allNotes = notes
                    _uiState.value = _uiState.value.copy(
                        notes = filterAndSort(notes),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = UiError.Failed(FailedAction.LOAD_NOTES, e.message)
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        refreshNotes()
    }

    fun onTagFilterChanged(tagId: Long, isSelected: Boolean) {
        val newTags = if (isSelected) {
            _uiState.value.selectedTagIds + tagId
        } else {
            _uiState.value.selectedTagIds - tagId
        }
        _uiState.value = _uiState.value.copy(selectedTagIds = newTags)
        refreshNotes()
    }

    fun onClearTagFilter() {
        _uiState.value = _uiState.value.copy(selectedTagIds = emptySet())
        refreshNotes()
    }

    fun onSortChanged(sortOption: SortOption) {
        _uiState.value = _uiState.value.copy(sortBy = sortOption)
        refreshNotes()
    }

    fun onDeleteNote(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId).onSuccess {
                allNotes = allNotes.filter { it.id != noteId }
                _uiState.value = _uiState.value.copy(
                    notes = _uiState.value.notes.filter { it.id != noteId }
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    error = UiError.Failed(FailedAction.DELETE_NOTE, e.message)
                )
            }
        }
    }

    private fun refreshNotes() {
        _uiState.value = _uiState.value.copy(notes = filterAndSort(allNotes))
    }

    private fun filterAndSort(notes: List<Note>): List<Note> {
        val state = _uiState.value

        var filtered = notes

        if (state.searchQuery.isNotEmpty()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter { note ->
                note.title.lowercase().contains(query) ||
                note.content.lowercase().contains(query)
            }
        }

        if (state.selectedTagIds.isNotEmpty()) {
            filtered = filtered.filter { note ->
                note.tags.any { it.id in state.selectedTagIds }
            }
        }

        return when (state.sortBy) {
            SortOption.UPDATED_DESC -> filtered.sortedByDescending { it.updatedAt }
            SortOption.UPDATED_ASC -> filtered.sortedBy { it.updatedAt }
            SortOption.TITLE_ASC -> filtered.sortedBy { it.title }
        }
    }
}
