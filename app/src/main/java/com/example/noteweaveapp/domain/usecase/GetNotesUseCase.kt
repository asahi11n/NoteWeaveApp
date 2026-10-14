package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = noteRepository.getAllNotesFlow()
}
