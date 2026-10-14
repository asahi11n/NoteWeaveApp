package com.example.noteweaveapp.presentation.state

sealed interface NoteDetailUiEvent {
    data class ShowMessage(val message: String) : NoteDetailUiEvent
}
