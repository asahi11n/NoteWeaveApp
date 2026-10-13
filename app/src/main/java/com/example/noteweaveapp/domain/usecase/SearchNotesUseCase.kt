package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

class SearchNotesUseCase(private val noteRepository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return if (query.isBlank()) {
            noteRepository.getAllNotesFlow()
        } else {
            noteRepository.searchNotes(query)
        }
    }
}
