package com.example.noteweaveapp.data.repository

import com.example.noteweaveapp.data.db.dao.NoteDao
import com.example.noteweaveapp.data.db.entity.NoteEntity
import com.example.noteweaveapp.data.db.entity.NoteTagCrossRef
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotesFlow(): Flow<List<Note>> =
        noteDao.getAllNotesWithTagsFlow().map { noteWithTagsList ->
            noteWithTagsList.map { noteWithTags ->
                Note(
                    id = noteWithTags.note.id,
                    title = noteWithTags.note.title,
                    content = noteWithTags.note.content,
                    summary = noteWithTags.note.summary,
                    createdAt = noteWithTags.note.createdAt,
                    updatedAt = noteWithTags.note.updatedAt,
                    isFavorite = noteWithTags.note.isFavorite,
                    tags = noteWithTags.tags.map { Tag(it.id, it.name) }
                )
            }
        }

    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteWithTagsById(noteId)?.let { noteWithTags ->
            Note(
                id = noteWithTags.note.id,
                title = noteWithTags.note.title,
                content = noteWithTags.note.content,
                summary = noteWithTags.note.summary,
                createdAt = noteWithTags.note.createdAt,
                updatedAt = noteWithTags.note.updatedAt,
                isFavorite = noteWithTags.note.isFavorite,
                tags = noteWithTags.tags.map { Tag(it.id, it.name) }
            )
        }
    }

    suspend fun createNote(note: Note): Long {
        val noteEntity = NoteEntity(
            title = note.title,
            content = note.content,
            summary = note.summary,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            isFavorite = note.isFavorite
        )
        return noteDao.insert(noteEntity)
    }

    suspend fun updateNote(note: Note) {
        val noteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content,
            summary = note.summary,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            isFavorite = note.isFavorite
        )
        noteDao.update(noteEntity)
    }

    suspend fun deleteNote(noteId: Long) {
        noteDao.deleteById(noteId)
    }

    fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotesWithTags("%$query%").map { noteWithTagsList ->
            noteWithTagsList.map { noteWithTags ->
                Note(
                    id = noteWithTags.note.id,
                    title = noteWithTags.note.title,
                    content = noteWithTags.note.content,
                    summary = noteWithTags.note.summary,
                    createdAt = noteWithTags.note.createdAt,
                    updatedAt = noteWithTags.note.updatedAt,
                    isFavorite = noteWithTags.note.isFavorite,
                    tags = noteWithTags.tags.map { Tag(it.id, it.name) }
                )
            }
        }

    fun getNotesByTags(tagIds: List<Long>): Flow<List<Note>> =
        noteDao.getNotesByTags(tagIds).map { noteWithTagsList ->
            noteWithTagsList.map { noteWithTags ->
                Note(
                    id = noteWithTags.note.id,
                    title = noteWithTags.note.title,
                    content = noteWithTags.note.content,
                    summary = noteWithTags.note.summary,
                    createdAt = noteWithTags.note.createdAt,
                    updatedAt = noteWithTags.note.updatedAt,
                    isFavorite = noteWithTags.note.isFavorite,
                    tags = noteWithTags.tags.map { Tag(it.id, it.name) }
                )
            }
        }

    suspend fun attachTagToNote(noteId: Long, tagId: Long) {
        noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    suspend fun removeTagFromNote(noteId: Long, tagId: Long) {
        noteDao.removeTagFromNote(noteId, tagId)
    }
}
