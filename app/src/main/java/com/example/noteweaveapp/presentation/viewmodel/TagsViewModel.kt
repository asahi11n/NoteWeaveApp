package com.example.noteweaveapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteweaveapp.domain.usecase.CreateTagUseCase
import com.example.noteweaveapp.domain.usecase.DuplicateTagNameException
import com.example.noteweaveapp.domain.usecase.EmptyTagNameException
import com.example.noteweaveapp.domain.usecase.GetAllTagsUseCase
import com.example.noteweaveapp.domain.usecase.GetNotesUseCase
import com.example.noteweaveapp.presentation.state.FailedAction
import com.example.noteweaveapp.presentation.state.TagsUiEvent
import com.example.noteweaveapp.presentation.state.TagsUiState
import com.example.noteweaveapp.presentation.state.UiError
import com.example.noteweaveapp.util.tags.TagUsageCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val createTagUseCase: CreateTagUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagsUiState())
    val uiState: StateFlow<TagsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TagsUiEvent>()
    val events: SharedFlow<TagsUiEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(getAllTagsUseCase(), getNotesUseCase()) { tags, notes ->
                TagUsageCounter.count(tags, notes)
            }.collect { usages ->
                _uiState.value = _uiState.value.copy(usages = usages)
            }
        }
    }

    fun clearCreateError() {
        _uiState.value = _uiState.value.copy(createError = null)
    }

    fun onCreateTag(name: String) {
        viewModelScope.launch {
            createTagUseCase(name).onSuccess {
                _uiState.value = _uiState.value.copy(createError = null)
                _events.emit(TagsUiEvent.TagCreated)
            }.onFailure { e ->
                val error = when (e) {
                    is EmptyTagNameException -> UiError.TagEmpty
                    is DuplicateTagNameException -> UiError.TagExists
                    else -> UiError.Failed(FailedAction.CREATE_TAG, e.message)
                }
                _uiState.value = _uiState.value.copy(createError = error)
            }
        }
    }
}
