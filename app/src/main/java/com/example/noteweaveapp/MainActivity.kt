package com.example.noteweaveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.noteweaveapp.data.ai.LocalNoteAiProcessor
import com.example.noteweaveapp.data.db.NoteWeaveDatabase
import com.example.noteweaveapp.data.repository.NoteRepository
import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.usecase.CreateNoteUseCase
import com.example.noteweaveapp.domain.usecase.DeleteNoteUseCase
import com.example.noteweaveapp.domain.usecase.GenerateSummaryUseCase
import com.example.noteweaveapp.domain.usecase.GenerateTagSuggestionsUseCase
import com.example.noteweaveapp.domain.usecase.GetNoteByIdUseCase
import com.example.noteweaveapp.domain.usecase.GetNotesUseCase
import com.example.noteweaveapp.domain.usecase.UpdateNoteUseCase
import com.example.noteweaveapp.presentation.ui.screens.NoteDetailScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteEditScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteListScreen
import com.example.noteweaveapp.presentation.ui.theme.NoteWeaveTheme
import com.example.noteweaveapp.presentation.viewmodel.NoteDetailViewModel
import com.example.noteweaveapp.presentation.viewmodel.NoteEditViewModel
import com.example.noteweaveapp.presentation.viewmodel.NoteListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteWeaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = NoteWeaveDatabase.getInstance(applicationContext)
                    val noteRepository = NoteRepository(db.noteDao())
                    val tagRepository = TagRepository(db.tagDao())

                    val getNotesUseCase = GetNotesUseCase(noteRepository)
                    val createNoteUseCase = CreateNoteUseCase(noteRepository, tagRepository)
                    val updateNoteUseCase = UpdateNoteUseCase(noteRepository, tagRepository)
                    val deleteNoteUseCase = DeleteNoteUseCase(noteRepository)
                    val getNoteByIdUseCase = GetNoteByIdUseCase(noteRepository)
                    val getAllTagsUseCase = com.example.noteweaveapp.domain.usecase.GetAllTagsUseCase(tagRepository)
                    val getRelatedNotesUseCase = com.example.noteweaveapp.domain.usecase.GetRelatedNotesUseCase(noteRepository)
                    val aiProcessor = LocalNoteAiProcessor()
                    val generateSummaryUseCase = GenerateSummaryUseCase(aiProcessor)
                    val generateTagSuggestionsUseCase = GenerateTagSuggestionsUseCase(aiProcessor)

                    val currentScreen = remember { mutableStateOf<Screen>(Screen.List) }

                    when (val screen = currentScreen.value) {
                        Screen.List -> {
                            NoteListScreen(
                                viewModel = NoteListViewModel(getNotesUseCase, deleteNoteUseCase, getAllTagsUseCase),
                                onNoteClick = { noteId ->
                                    currentScreen.value = Screen.Detail(noteId)
                                },
                                onCreateNote = {
                                    currentScreen.value = Screen.Edit(null)
                                }
                            )
                        }
                        is Screen.Detail -> {
                            NoteDetailScreen(
                                viewModel = NoteDetailViewModel(getNoteByIdUseCase, getNotesUseCase, getRelatedNotesUseCase, generateSummaryUseCase, generateTagSuggestionsUseCase),
                                noteId = screen.noteId,
                                onBackClick = {
                                    currentScreen.value = Screen.List
                                },
                                onEditClick = { noteId ->
                                    currentScreen.value = Screen.Edit(noteId)
                                }
                            )
                        }
                        is Screen.Edit -> {
                            NoteEditScreen(
                                viewModel = NoteEditViewModel(
                                    createNoteUseCase,
                                    updateNoteUseCase,
                                    getNoteByIdUseCase,
                                    tagRepository
                                ),
                                noteId = screen.noteId,
                                onBackClick = {
                                    currentScreen.value = Screen.List
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    sealed class Screen {
        object List : Screen()
        data class Detail(val noteId: Long) : Screen()
        data class Edit(val noteId: Long?) : Screen()
    }

    private fun viewModelFactory(factory: () -> Any) = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return factory() as T
        }
    }
}