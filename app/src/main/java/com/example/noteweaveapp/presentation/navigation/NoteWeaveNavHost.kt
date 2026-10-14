package com.example.noteweaveapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteweaveapp.presentation.ui.screens.NoteDetailScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteEditScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteListScreen

object NoteWeaveRoutes {
    const val LIST = "list"
    const val DETAIL = "detail/{noteId}"
    const val EDIT = "edit?noteId={noteId}"

    fun detail(noteId: Long) = "detail/$noteId"

    // 0 は新規作成を表す
    fun edit(noteId: Long) = "edit?noteId=$noteId"
}

@Composable
fun NoteWeaveNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NoteWeaveRoutes.LIST) {
        composable(NoteWeaveRoutes.LIST) {
            NoteListScreen(
                viewModel = hiltViewModel(),
                onNoteClick = { noteId -> navController.navigate(NoteWeaveRoutes.detail(noteId)) },
                onCreateNote = { navController.navigate(NoteWeaveRoutes.edit(0L)) }
            )
        }

        composable(
            route = NoteWeaveRoutes.DETAIL,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) {
            NoteDetailScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onEditClick = { noteId -> navController.navigate(NoteWeaveRoutes.edit(noteId)) }
            )
        }

        composable(
            route = NoteWeaveRoutes.EDIT,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            NoteEditScreen(
                viewModel = hiltViewModel(),
                noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
