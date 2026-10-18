package com.example.noteweaveapp.util.tags

import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag

data class TagUsage(
    val tag: Tag,
    val noteCount: Int
)

object TagUsageCounter {
    // ノート数の多い順、同数なら名前順に並べる
    fun count(tags: List<Tag>, notes: List<Note>): List<TagUsage> =
        tags.map { tag ->
            TagUsage(tag, notes.count { note -> note.tags.any { it.id == tag.id } })
        }.sortedWith(
            compareByDescending<TagUsage> { it.noteCount }.thenBy { it.tag.name.lowercase() }
        )
}
