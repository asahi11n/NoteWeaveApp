package com.example.noteweaveapp.data.ai

import com.example.noteweaveapp.domain.ai.NoteAiProcessor

class LocalNoteAiProcessor : NoteAiProcessor {
    override suspend fun summarize(text: String): String {
        if (text.isBlank()) {
            return ""
        }

        val sentences = text.split(Regex("[。！？\n]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return if (sentences.isEmpty()) {
            ""
        } else {
            val summaryLength = maxOf(1, sentences.size / 3)
            sentences.take(summaryLength).joinToString("。") + "。"
        }
    }

    override suspend fun suggestTags(text: String, existingTags: List<String>): List<String> {
        if (text.isBlank()) {
            return emptyList()
        }

        val commonKeywords = listOf(
            "Android", "Kotlin", "Java", "API", "Database", "Room", "Compose",
            "ViewModel", "Repository", "Coroutine", "Flow", "StateFlow",
            "UI", "UX", "Design", "Architecture", "Testing", "Unit Test",
            "Integration", "Performance", "Security", "Bug", "Feature",
            "Framework", "Library", "Tool", "Development", "Production"
        )

        val existingSet = existingTags.map { it.lowercase() }.toSet()
        return commonKeywords
            .filter { keyword ->
                text.contains(keyword, ignoreCase = true) &&
                keyword.lowercase() !in existingSet
            }
            .take(5)
    }
}
