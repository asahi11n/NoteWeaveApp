package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val noteRepository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = noteRepository.getAllNotesFlow()
}
