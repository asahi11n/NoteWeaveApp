package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.domain.ai.NoteAiProcessor

class GenerateSummaryUseCase @Inject constructor(private val aiProcessor: NoteAiProcessor) {
    suspend operator fun invoke(text: String): String {
        return aiProcessor.summarize(text)
    }
}
