package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.domain.usecase.AddTagsToNoteUseCase
import com.example.noteweaveapp.domain.usecase.GenerateSummaryUseCase
import com.example.noteweaveapp.domain.usecase.GenerateTagSuggestionsUseCase
import com.example.noteweaveapp.domain.usecase.GetNotesUseCase
import com.example.noteweaveapp.domain.usecase.GetRelatedNotesUseCase
import com.example.noteweaveapp.presentation.state.AiProcessState
import com.example.noteweaveapp.presentation.state.NoteDetailUiEvent
import com.example.noteweaveapp.presentation.state.NoteDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getRelatedNotesUseCase: GetRelatedNotesUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val generateTagSuggestionsUseCase: GenerateTagSuggestionsUseCase,
    private val addTagsToNoteUseCase: AddTagsToNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle.get<Long>("noteId"))

    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NoteDetailUiEvent>()
    val events: SharedFlow<NoteDetailUiEvent> = _events.asSharedFlow()

    init {
        observeNote()
    }

    // 全ノートを監視し、編集後の DB 変更も画面に反映する
    private fun observeNote() {
        viewModelScope.launch {
            try {
                getNotesUseCase().collectLatest { allNotes ->
                    val note = allNotes.find { it.id == noteId }
                    if (note == null) {
                        _uiState.value = NoteDetailUiState.Error("Note not found")
                        return@collectLatest
                    }
                    val relatedNotes = getRelatedNotesUseCase(noteId, allNotes)
                    // 既存の AI 処理の状態は保持したまま、ノート本体だけ差し替える
                    _uiState.value = when (val current = _uiState.value) {
                        is NoteDetailUiState.Success -> current.copy(
                            note = note,
                            relatedNotes = relatedNotes
                        )
                        else -> NoteDetailUiState.Success(
                            note = note,
                            relatedNotes = relatedNotes
                        )
                    }
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

    // 採用したタグはノートに追加する。失敗時は Snackbar で通知する
    fun onTagsAdopted(tags: List<String>) {
        val currentState = _uiState.value
        if (currentState !is NoteDetailUiState.Success) return

        viewModelScope.launch {
            val result = addTagsToNoteUseCase(currentState.note.id, tags)
            // 画面の反映は notes の監視で行われるため、ここでは AI 側の状態だけ更新する
            val latest = _uiState.value as? NoteDetailUiState.Success ?: return@launch
            _uiState.value = latest.copy(aiTagsState = AiProcessState.Idle, suggestedTags = emptyList())
            result.onFailure { e ->
                _events.emit(NoteDetailUiEvent.ShowMessage("Failed to save tags: ${e.message}"))
            }
        }
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
