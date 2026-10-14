package com.example.noteweaveapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.noteweaveapp.data.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: TagEntity): Long

    @Insert
    suspend fun insertAll(tags: List<TagEntity>)

    @androidx.room.Update
    suspend fun update(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteById(tagId: Long)

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TagEntity?

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): TagEntity?

    @Query("SELECT * FROM tags")
    fun getAllTagsFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN note_tag_cross_ref ntcr ON t.id = ntcr.tagId
        WHERE ntcr.noteId = :noteId
    """)
    suspend fun getTagsForNote(noteId: Long): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN note_tag_cross_ref ntcr ON t.id = ntcr.tagId
        WHERE ntcr.noteId = :noteId
    """)
    fun getTagsForNoteFlow(noteId: Long): Flow<List<TagEntity>>

    @Query("SELECT COUNT(*) FROM tags WHERE name = :name")
    suspend fun countByName(name: String): Int

    @Query("""
        SELECT COUNT(*) FROM note_tag_cross_ref
        WHERE tagId = :tagId
    """)
    suspend fun countNotesWithTag(tagId: Long): Int
}
