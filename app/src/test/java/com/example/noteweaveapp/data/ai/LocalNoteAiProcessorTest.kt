package com.example.noteweaveapp.data.ai

import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEmpty
import kotlin.test.assertTrue

class LocalNoteAiProcessorTest {

    private val processor = LocalNoteAiProcessor()

    @Test
    fun testSummarize_withValidText() = runBlocking {
        val text = "Android is great。Kotlin is awesome。Compose is modern。"
        val summary = processor.summarize(text)

        assertNotEmpty(summary)
        assertTrue(summary.contains("Android"))
    }

    @Test
    fun testSummarize_withEmptyText() = runBlocking {
        val summary = processor.summarize("")
        assertEquals("", summary)
    }

    @Test
    fun testSummarize_returnsFirstThird() = runBlocking {
        val text = "First sentence。Second sentence。Third sentence。Fourth sentence。Fifth sentence。Sixth sentence。"
        val summary = processor.summarize(text)

        assertTrue(summary.isNotEmpty())
        assertTrue(summary.contains("First"))
    }

    @Test
    fun testSuggestTags_withAndroidContent() = runBlocking {
        val text = "This is about Android development and Kotlin programming"
        val tags = processor.suggestTags(text, emptyList())

        assertNotEmpty(tags)
        assertTrue(tags.contains("Android") || tags.contains("Kotlin"))
    }

    @Test
    fun testSuggestTags_excludesExistingTags() = runBlocking {
        val text = "This is about Android development and Kotlin programming"
        val existingTags = listOf("Android")
        val tags = processor.suggestTags(text, existingTags)

        assertTrue(!tags.contains("Android"))
    }

    @Test
    fun testSuggestTags_withEmptyText() = runBlocking {
        val tags = processor.suggestTags("", emptyList())
        assertEquals(0, tags.size)
    }

    @Test
    fun testSuggestTags_returnsMaxFive() = runBlocking {
        val text = "Android Kotlin API Database Room Compose ViewModel Repository UseCase"
        val tags = processor.suggestTags(text, emptyList())

        assertTrue(tags.size <= 5)
    }
}
