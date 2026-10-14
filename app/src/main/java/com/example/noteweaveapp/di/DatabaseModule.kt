package com.example.noteweaveapp.di

import android.content.Context
import androidx.room.Room
import com.example.noteweaveapp.data.db.NoteWeaveDatabase
import com.example.noteweaveapp.data.db.dao.NoteDao
import com.example.noteweaveapp.data.db.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteWeaveDatabase =
        Room.databaseBuilder(
            context,
            NoteWeaveDatabase::class.java,
            "notweave_database"
        ).build()

    @Provides
    fun provideNoteDao(database: NoteWeaveDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideTagDao(database: NoteWeaveDatabase): TagDao = database.tagDao()
}
