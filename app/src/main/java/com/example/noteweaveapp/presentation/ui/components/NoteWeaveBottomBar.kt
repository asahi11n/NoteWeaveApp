package com.example.noteweaveapp.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.noteweaveapp.R

enum class MainTab { HOME, TAGS }

@Composable
fun NoteWeaveBottomBar(
    selected: MainTab,
    onHomeClick: () -> Unit,
    onTagsClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == MainTab.HOME,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_home)) }
        )
        NavigationBarItem(
            selected = selected == MainTab.TAGS,
            onClick = onTagsClick,
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_tags)) }
        )
    }
}
