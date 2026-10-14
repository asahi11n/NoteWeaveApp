package com.example.noteweaveapp.di

import com.example.noteweaveapp.data.ai.LocalNoteAiProcessor
import com.example.noteweaveapp.domain.ai.NoteAiProcessor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    abstract fun bindNoteAiProcessor(processor: LocalNoteAiProcessor): NoteAiProcessor
}
