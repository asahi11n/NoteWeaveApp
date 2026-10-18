package com.example.noteweaveapp.util.tags

import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag
import kotlin.test.Test
import kotlin.test.assertEquals

class TagUsageCounterTest {

    private val kotlinTag = Tag(id = 1, name = "kotlin")
    private val androidTag = Tag(id = 2, name = "android")
    private val unusedTag = Tag(id = 3, name = "unused")

    private fun note(id: Long, vararg tags: Tag) = Note(
        id = id,
        title = "title$id",
        content = "content$id",
        createdAt = 0L,
        updatedAt = 0L,
        tags = tags.toList()
    )

    @Test
    fun count_countsNotesPerTag() {
        val notes = listOf(
            note(1, kotlinTag, androidTag),
            note(2, kotlinTag)
        )

        val result = TagUsageCounter.count(listOf(kotlinTag, androidTag, unusedTag), notes)

        assertEquals(
            listOf(
                TagUsage(kotlinTag, 2),
                TagUsage(androidTag, 1),
                TagUsage(unusedTag, 0)
            ),
            result
        )
    }

    @Test
    fun count_sortsByCountThenName() {
        val notes = listOf(note(1, androidTag))

        val result = TagUsageCounter.count(listOf(kotlinTag, androidTag), notes)

        assertEquals(listOf("android", "kotlin"), result.map { it.tag.name })
    }

    @Test
    fun count_returnsEmptyWhenNoTags() {
        assertEquals(emptyList(), TagUsageCounter.count(emptyList(), emptyList()))
    }
}
