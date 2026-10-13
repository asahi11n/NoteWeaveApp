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

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotesFlow(): Flow<List<NoteEntity>>

    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotesWithTagsFlow(): Flow<List<NoteWithTags>>

    @Query("SELECT DISTINCT n.* FROM notes n WHERE n.title LIKE :searchQuery OR n.content LIKE :searchQuery ORDER BY n.updatedAt DESC")
    fun searchNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Transaction
    @Query("""
        SELECT DISTINCT n.* FROM notes n
        WHERE (n.title LIKE :searchQuery OR n.content LIKE :searchQuery)
        ORDER BY n.updatedAt DESC
    """)
    fun searchNotesWithTags(searchQuery: String): Flow<List<NoteWithTags>>

    @Transaction
    @Query("""
        SELECT n.* FROM notes n
        INNER JOIN note_tag_cross_ref ntc ON n.id = ntc.noteId
        INNER JOIN tags t ON ntc.tagId = t.id
        WHERE t.id IN (:tagIds)
        GROUP BY n.id
        ORDER BY n.updatedAt DESC
    """)
    fun getNotesByTags(tagIds: List<Long>): Flow<List<NoteWithTags>>

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getNotesOrderByTitle(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt ASC")
    fun getNotesOrderByOldest(): Flow<List<NoteEntity>>

    @Insert
    suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun removeTagFromNote(noteId: Long, tagId: Long)
}
