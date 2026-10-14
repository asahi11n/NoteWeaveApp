package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.util.scoring.RelatedNoteScorer

class GetRelatedNotesUseCase(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteId: Long, allNotes: List<Note>): List<Note> {
        val currentNote = noteRepository.getNoteById(noteId) ?: return emptyList()
        return RelatedNoteScorer.scoreRelatedNotes(currentNote, allNotes, limit = 5)
    }
}
