package com.example.noteweaveapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteweaveapp.presentation.ui.components.MainTab
import com.example.noteweaveapp.presentation.ui.components.NoteWeaveBottomBar
import com.example.noteweaveapp.presentation.ui.screens.NoteDetailScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteEditScreen
import com.example.noteweaveapp.presentation.ui.screens.NoteListScreen
import com.example.noteweaveapp.presentation.ui.screens.TagsScreen

object NoteWeaveRoutes {
    const val LIST = "list?tagId={tagId}"
    const val DETAIL = "detail/{noteId}"
    const val EDIT = "edit?noteId={noteId}"
    const val TAGS = "tags"

    // 指定なし（-1）でホームを開く
    fun home() = "list?tagId=-1"

    fun listForTag(tagId: Long) = "list?tagId=$tagId"

    fun detail(noteId: Long) = "detail/$noteId"

    // 0 は新規作成を表す
    fun edit(noteId: Long) = "edit?noteId=$noteId"
}

@Composable
fun NoteWeaveNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedTab = if (currentRoute == NoteWeaveRoutes.TAGS) MainTab.TAGS else MainTab.HOME

    // 下部ナビゲーションの切り替え（同じタブを重ねず、状態は保存して戻れるようにする）
    fun selectTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val bottomBar: @Composable () -> Unit = {
        NoteWeaveBottomBar(
            selected = selectedTab,
            onHomeClick = { selectTab(NoteWeaveRoutes.home()) },
            onTagsClick = { selectTab(NoteWeaveRoutes.TAGS) }
        )
    }

    NavHost(navController = navController, startDestination = NoteWeaveRoutes.LIST) {
        composable(
            route = NoteWeaveRoutes.LIST,
            arguments = listOf(
                navArgument("tagId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            NoteListScreen(
                viewModel = hiltViewModel(),
                onNoteClick = { noteId -> navController.navigate(NoteWeaveRoutes.detail(noteId)) },
                onCreateNote = { navController.navigate(NoteWeaveRoutes.edit(0L)) },
                bottomBar = bottomBar
            )
        }

        composable(NoteWeaveRoutes.TAGS) {
            TagsScreen(
                viewModel = hiltViewModel(),
                onTagClick = { tagId -> navController.navigate(NoteWeaveRoutes.listForTag(tagId)) },
                bottomBar = bottomBar
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
