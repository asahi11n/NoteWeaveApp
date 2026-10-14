package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.util.validation.NoteValidator
import com.example.noteweaveapp.util.validation.ValidationResult

class UpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(
        noteId: Long,
        title: String,
        content: String,
        tagNames: List<String> = emptyList()
    ): Result<Unit> {
        val validation = NoteValidator.validateNote(title, content)
        if (validation != ValidationResult.Valid) {
            return Result.failure(
                IllegalArgumentException(
                    when (validation) {
                        is ValidationResult.InvalidTitle -> validation.message
                        is ValidationResult.InvalidContent -> validation.message
                        else -> "Invalid input"
                    }
                )
            )
        }

        val currentNote = noteRepository.getNoteById(noteId)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        val tags = tagNames.map { tagRepository.getOrCreateTag(it) }

        val updatedNote = Note(
            id = noteId,
            title = title.trim(),
            content = content.trim(),
            summary = currentNote.summary,
            createdAt = currentNote.createdAt,
            updatedAt = System.currentTimeMillis(),
            isFavorite = currentNote.isFavorite,
            tags = tags
        )

        noteRepository.updateNote(updatedNote)

        currentNote.tags.forEach { oldTag ->
            if (tags.find { it.id == oldTag.id } == null) {
                noteRepository.removeTagFromNote(noteId, oldTag.id)
            }
        }

        tags.forEach { newTag ->
            if (currentNote.tags.find { it.id == newTag.id } == null) {
                noteRepository.attachTagToNote(noteId, newTag.id)
            }
        }

        return Result.success(Unit)
    }
}
