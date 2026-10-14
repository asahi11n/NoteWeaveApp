package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.data.repository.NoteRepository

class DeleteNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteId: Long): Result<Unit> {
        return try {
            noteRepository.deleteNote(noteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
