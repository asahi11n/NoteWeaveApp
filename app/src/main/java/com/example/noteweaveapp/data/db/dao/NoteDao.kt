package com.example.noteweaveapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.noteweaveapp.data.db.entity.NoteEntity
import com.example.noteweaveapp.data.db.entity.NoteTagCrossRef
import com.example.noteweaveapp.data.db.entity.NoteWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteById(noteId: Long)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteWithTagsById(noteId: Long): NoteWithTags?

    @Query("SELECT * FROM notes")
    fun getAllNotesFlow(): Flow<List<NoteEntity>>

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTagsFlow(): Flow<List<NoteWithTags>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Transaction
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotesWithTags(searchQuery: String): Flow<List<NoteWithTags>>

    @Transaction
    @Query("""
        SELECT DISTINCT n.* FROM notes n
        INNER JOIN note_tag_cross_ref ntcr ON n.id = ntcr.noteId
        WHERE ntcr.tagId IN (:tagIds)
    """)
    fun getNotesByTags(tagIds: List<Long>): Flow<List<NoteWithTags>>

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getNotesOrderByTitle(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY createdAt ASC")
    fun getNotesOrderByOldest(): Flow<List<NoteEntity>>

    @Insert
    suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun removeTagFromNote(noteId: Long, tagId: Long)
}
