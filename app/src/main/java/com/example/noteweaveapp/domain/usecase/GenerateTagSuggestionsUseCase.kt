package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.domain.ai.NoteAiProcessor
import com.example.noteweaveapp.domain.model.Tag

class GenerateTagSuggestionsUseCase @Inject constructor(private val aiProcessor: NoteAiProcessor) {
    suspend operator fun invoke(text: String, existingTags: List<Tag>): List<String> {
        val existingTagNames = existingTags.map { it.name }
        return aiProcessor.suggestTags(text, existingTagNames)
    }
}
