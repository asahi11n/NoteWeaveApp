package com.example.noteweaveapp.util.scoring

import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RelatedNoteScorerTest {

    private val tag1 = Tag(1, "Android")
    private val tag2 = Tag(2, "Kotlin")
    private val tag3 = Tag(3, "Compose")
    private val tag4 = Tag(4, "Database")

    @Test
    fun testScoreRelatedNotes_withCommonTags() {
        val currentNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1, tag2)
        )

        val relatedNote1 = Note(
            id = 2,
            title = "Note 2",
            content = "Content 2",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1, tag2, tag3)
        )

        val relatedNote2 = Note(
            id = 3,
            title = "Note 3",
            content = "Content 3",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1)
        )

        val unrelatedNote = Note(
            id = 4,
            title = "Note 4",
            content = "Content 4",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag4)
        )

        val result = RelatedNoteScorer.scoreRelatedNotes(
            currentNote,
            listOf(relatedNote1, relatedNote2, unrelatedNote)
        )

        assertEquals(2, result.size)
        assertTrue(result[0].id == 2L)
        assertTrue(result[1].id == 3L)
    }

    @Test
    fun testScoreRelatedNotes_excludesSelf() {
        val currentNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1)
        )

        val result = RelatedNoteScorer.scoreRelatedNotes(
            currentNote,
            listOf(currentNote)
        )

        assertEquals(0, result.size)
    }

    @Test
    fun testScoreRelatedNotes_withNoCommonTags() {
        val currentNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1)
        )

        val unrelatedNote = Note(
            id = 2,
            title = "Note 2",
            content = "Content 2",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag4)
        )

        val result = RelatedNoteScorer.scoreRelatedNotes(
            currentNote,
            listOf(unrelatedNote)
        )

        assertEquals(0, result.size)
    }

    @Test
    fun testCalculateSimilarityScore() {
        val note1 = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1, tag2)
        )

        val note2 = Note(
            id = 2,
            title = "Note 2",
            content = "Content 2",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            tags = listOf(tag1, tag2, tag3)
        )

        val score = RelatedNoteScorer.calculateSimilarityScore(note1, note2)
        assertEquals(20, score)
    }
}
