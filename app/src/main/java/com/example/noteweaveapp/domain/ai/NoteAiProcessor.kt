package com.example.noteweaveapp.domain.ai

interface NoteAiProcessor {
    suspend fun summarize(text: String): String
    suspend fun suggestTags(text: String, existingTags: List<String>): List<String>
}
