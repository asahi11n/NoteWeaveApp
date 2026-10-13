package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.domain.model.Note

class GetNoteByIdUseCase(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteId: Long): Note? =
        noteRepository.getNoteById(noteId)
}
