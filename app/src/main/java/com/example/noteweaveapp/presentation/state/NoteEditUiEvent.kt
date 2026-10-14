package com.example.noteweaveapp.presentation.state

sealed interface NoteEditUiEvent {
    data object SaveCompleted : NoteEditUiEvent
}
