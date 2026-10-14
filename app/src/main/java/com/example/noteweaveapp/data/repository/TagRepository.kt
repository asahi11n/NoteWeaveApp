package com.example.noteweaveapp.data.repository

import javax.inject.Inject
import com.example.noteweaveapp.data.db.dao.TagDao
import com.example.noteweaveapp.data.db.entity.TagEntity
import com.example.noteweaveapp.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepository @Inject constructor(private val tagDao: TagDao) {
    fun getAllTagsFlow(): Flow<List<Tag>> =
        tagDao.getAllTagsFlow().map { tagEntities ->
            tagEntities.map { Tag(it.id, it.name) }
        }

    suspend fun getAllTags(): List<Tag> =
        tagDao.getAllTags().map { Tag(it.id, it.name) }

    suspend fun createTag(tagName: String): Long {
        val trimmed = Tag.trimmed(tagName)
        val existing = tagDao.getTagByName(trimmed)
        return if (existing != null) {
            existing.id
        } else {
            tagDao.insert(TagEntity(name = trimmed))
        }
    }

    suspend fun getOrCreateTag(tagName: String): Tag {
        val trimmed = Tag.trimmed(tagName)
        val existing = tagDao.getTagByName(trimmed)
        return if (existing != null) {
            Tag(existing.id, existing.name)
        } else {
            val id = tagDao.insert(TagEntity(name = trimmed))
            Tag(id, trimmed)
        }
    }

    suspend fun getTagById(tagId: Long): Tag? =
        tagDao.getTagById(tagId)?.let { Tag(it.id, it.name) }

    suspend fun getTagByName(name: String): Tag? {
        val trimmed = Tag.trimmed(name)
        return tagDao.getTagByName(trimmed)?.let { Tag(it.id, it.name) }
    }

    suspend fun getTagsForNote(noteId: Long): List<Tag> =
        tagDao.getTagsForNote(noteId).map { Tag(it.id, it.name) }

    fun getTagsForNoteFlow(noteId: Long): Flow<List<Tag>> =
        tagDao.getTagsForNoteFlow(noteId).map { tagEntities ->
            tagEntities.map { Tag(it.id, it.name) }
        }

    suspend fun deleteTag(tagId: Long) {
        tagDao.deleteById(tagId)
    }

    suspend fun isTagNameExists(tagName: String): Boolean {
        val trimmed = Tag.trimmed(tagName)
        return tagDao.countByName(trimmed) > 0
    }

    suspend fun countNotesWithTag(tagId: Long): Int =
        tagDao.countNotesWithTag(tagId)
}
