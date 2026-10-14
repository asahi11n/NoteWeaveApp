package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag
import com.example.noteweaveapp.util.validation.NoteValidator
import com.example.noteweaveapp.util.validation.ValidationResult

class CreateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        tagNames: List<String> = emptyList()
    ): Result<Long> {
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

        val now = System.currentTimeMillis()
        val tags = tagNames.map { tagRepository.getOrCreateTag(it) }

        val note = Note(
            title = title.trim(),
            content = content.trim(),
            createdAt = now,
            updatedAt = now,
            tags = tags
        )

        val noteId = noteRepository.createNote(note)

        tags.forEach { tag ->
            noteRepository.attachTagToNote(noteId, tag.id)
        }

        return Result.success(noteId)
    }
}
