package com.example.noteweaveapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteweaveapp.data.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tags: List<TagEntity>)

    @Update
    suspend fun update(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteById(tagId: Long)

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TagEntity?

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): TagEntity?

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTagsFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags ORDER BY name ASC")
    suspend fun getAllTags(): List<TagEntity>

    @Query("SELECT t.* FROM tags t INNER JOIN note_tag_cross_ref ntc ON t.id = ntc.tagId WHERE ntc.noteId = :noteId ORDER BY t.name ASC")
    suspend fun getTagsForNote(noteId: Long): List<TagEntity>

    @Query("SELECT t.* FROM tags t INNER JOIN note_tag_cross_ref ntc ON t.id = ntc.tagId WHERE ntc.noteId = :noteId ORDER BY t.name ASC")
    fun getTagsForNoteFlow(noteId: Long): Flow<List<TagEntity>>

    @Query("SELECT COUNT(*) FROM tags WHERE name = TRIM(:name)")
    suspend fun countByName(name: String): Int

    @Query("SELECT COUNT(*) FROM note_tag_cross_ref WHERE tagId = :tagId")
    suspend fun countNotesWithTag(tagId: Long): Int
}
