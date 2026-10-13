package com.example.noteweaveapp.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.noteweaveapp.data.db.entity.NoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var db: NoteWeaveDatabase
    private lateinit var noteDao: com.example.noteweaveapp.data.db.dao.NoteDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, NoteWeaveDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndGetNote() = runBlocking {
        val note = NoteEntity(
            title = "Test",
            content = "Content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val id = noteDao.insert(note)
        val retrieved = noteDao.getNoteById(id)

        assertNotNull(retrieved)
        assertEquals("Test", retrieved?.title)
    }

    @Test
    fun testUpdateNote() = runBlocking {
        val note = NoteEntity(
            title = "Original",
            content = "Content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val id = noteDao.insert(note)
        val updated = note.copy(id = id, title = "Updated")
        noteDao.update(updated)

        val retrieved = noteDao.getNoteById(id)
        assertEquals("Updated", retrieved?.title)
    }

    @Test
    fun testDeleteNote() = runBlocking {
        val note = NoteEntity(
            title = "ToDelete",
            content = "Content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val id = noteDao.insert(note)
        noteDao.deleteById(id)

        val retrieved = noteDao.getNoteById(id)
        assertNull(retrieved)
    }

    @Test
    fun testGetAllNotes() = runBlocking {
        val note1 = NoteEntity(
            title = "Note1",
            content = "Content1",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val note2 = NoteEntity(
            title = "Note2",
            content = "Content2",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        noteDao.insert(note1)
        noteDao.insert(note2)

        val notes = noteDao.getAllNotesFlow().first()
        assertEquals(2, notes.size)
    }

    @Test
    fun testSearchNotes() = runBlocking {
        val note1 = NoteEntity(
            title = "Android",
            content = "Content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val note2 = NoteEntity(
            title = "iOS",
            content = "Content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        noteDao.insert(note1)
        noteDao.insert(note2)

        val results = noteDao.searchNotes("%Android%").first()
        assertEquals(1, results.size)
        assertEquals("Android", results[0].title)
    }
}
