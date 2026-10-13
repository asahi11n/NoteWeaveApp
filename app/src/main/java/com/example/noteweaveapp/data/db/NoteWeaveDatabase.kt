package com.example.noteweaveapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteweaveapp.data.db.dao.NoteDao
import com.example.noteweaveapp.data.db.dao.TagDao
import com.example.noteweaveapp.data.db.entity.NoteEntity
import com.example.noteweaveapp.data.db.entity.NoteTagCrossRef
import com.example.noteweaveapp.data.db.entity.TagEntity

@Database(
    entities = [
        NoteEntity::class,
        TagEntity::class,
        NoteTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoteWeaveDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao

    companion object {
        private var instance: NoteWeaveDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): NoteWeaveDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): NoteWeaveDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteWeaveDatabase::class.java,
                "noteweave_db"
            ).build()
        }
    }
}
