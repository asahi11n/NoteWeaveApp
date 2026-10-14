package com.example.noteweaveapp.presentation.state

sealed interface NoteDetailUiEvent {
    data class ShowError(val error: UiError) : NoteDetailUiEvent
}
