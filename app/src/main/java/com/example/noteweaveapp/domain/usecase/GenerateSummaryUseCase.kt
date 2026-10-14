package com.example.noteweaveapp.domain.usecase

import com.example.noteweaveapp.domain.ai.NoteAiProcessor

class GenerateSummaryUseCase(private val aiProcessor: NoteAiProcessor) {
    suspend operator fun invoke(text: String): String {
        return aiProcessor.summarize(text)
    }
}
