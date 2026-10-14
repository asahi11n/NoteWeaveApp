package com.example.noteweaveapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteweaveapp.data.db.dao.NoteDao
import com.example.noteweaveapp.data.db.dao.TagDao
import com.example.noteweaveapp.data.db.entity.NoteEntity
import com.example.noteweaveapp.data.db.entity.NoteTagCrossRef
import com.example.noteweaveapp.data.db.entity.TagEntity

@Database(
    entities = [NoteEntity::class, TagEntity::class, NoteTagCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class NoteWeaveDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
}
