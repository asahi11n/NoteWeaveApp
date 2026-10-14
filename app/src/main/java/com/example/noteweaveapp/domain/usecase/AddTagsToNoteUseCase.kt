package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.data.repository.TagRepository
import javax.inject.Inject

// タイトルや本文を変えずに、ノートへタグを追加する（updatedAt は更新しない）
class AddTagsToNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(noteId: Long, tagNames: List<String>): Result<Unit> {
        return try {
            val currentNote = noteRepository.getNoteById(noteId)
                ?: return Result.failure(IllegalArgumentException("Note not found"))
            val currentTagIds = currentNote.tags.map { it.id }.toSet()

            tagNames
                .filter { it.isNotBlank() }
                .map { tagRepository.getOrCreateTag(it) }
                .distinctBy { it.id }
                .filter { it.id !in currentTagIds }
                .forEach { noteRepository.attachTagToNote(noteId, it.id) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
