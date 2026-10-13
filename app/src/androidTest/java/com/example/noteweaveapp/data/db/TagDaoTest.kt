package com.example.noteweaveapp.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.noteweaveapp.data.db.entity.TagEntity
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
class TagDaoTest {

    private lateinit var db: NoteWeaveDatabase
    private lateinit var tagDao: com.example.noteweaveapp.data.db.dao.TagDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, NoteWeaveDatabase::class.java).build()
        tagDao = db.tagDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndGetTag() = runBlocking {
        val tag = TagEntity(name = "Android")
        val id = tagDao.insert(tag)

        val retrieved = tagDao.getTagById(id)
        assertNotNull(retrieved)
        assertEquals("Android", retrieved?.name)
    }

    @Test
    fun testGetTagByName() = runBlocking {
        val tag = TagEntity(name = "Kotlin")
        tagDao.insert(tag)

        val retrieved = tagDao.getTagByName("Kotlin")
        assertNotNull(retrieved)
        assertEquals("Kotlin", retrieved?.name)
    }

    @Test
    fun testGetAllTags() = runBlocking {
        tagDao.insert(TagEntity(name = "Tag1"))
        tagDao.insert(TagEntity(name = "Tag2"))
        tagDao.insert(TagEntity(name = "Tag3"))

        val tags = tagDao.getAllTagsFlow().first()
        assertEquals(3, tags.size)
    }

    @Test
    fun testDeleteTag() = runBlocking {
        val tag = TagEntity(name = "ToDelete")
        val id = tagDao.insert(tag)

        tagDao.deleteById(id)
        val retrieved = tagDao.getTagById(id)

        assertNull(retrieved)
    }

    @Test
    fun testDuplicateTagPrevention() = runBlocking {
        val tag1 = TagEntity(name = "Duplicate")
        val tag2 = TagEntity(name = "Duplicate")

        val id1 = tagDao.insert(tag1)
        val id2 = tagDao.insert(tag2)

        assertEquals(0, id2)
    }

    @Test
    fun testCountByName() = runBlocking {
        tagDao.insert(TagEntity(name = "TestTag"))

        val count = tagDao.countByName("TestTag")
        assertEquals(1, count)
    }
}
