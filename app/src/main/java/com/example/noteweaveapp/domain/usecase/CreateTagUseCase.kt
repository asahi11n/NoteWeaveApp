package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.model.Tag
import javax.inject.Inject

class EmptyTagNameException : Exception()

class DuplicateTagNameException : Exception()

class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    // タグ名は前後の空白を除き、大文字小文字を区別せず重複を防ぐ
    suspend operator fun invoke(name: String): Result<Tag> {
        val trimmed = Tag.trimmed(name)
        if (trimmed.isEmpty()) {
            return Result.failure(EmptyTagNameException())
        }
        return try {
            val exists = tagRepository.getAllTags().any { it.name.equals(trimmed, ignoreCase = true) }
            if (exists) {
                Result.failure(DuplicateTagNameException())
            } else {
                val id = tagRepository.createTag(trimmed)
                Result.success(Tag(id = id, name = trimmed))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
