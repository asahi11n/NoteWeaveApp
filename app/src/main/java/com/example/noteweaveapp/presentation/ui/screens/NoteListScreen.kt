package com.example.noteweaveapp.presentation.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.noteweaveapp.R
import androidx.compose.ui.platform.LocalContext
import com.example.noteweaveapp.presentation.ui.toMessage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.presentation.ui.components.SortOptionSelector
import com.example.noteweaveapp.presentation.ui.components.TagSelectorDialog
import com.example.noteweaveapp.presentation.viewmodel.NoteListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteListViewModel,
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showTagDialog by remember { mutableStateOf(false) }

    if (showTagDialog) {
        TagSelectorDialog(
            tags = uiState.allTags,
            selectedTagIds = uiState.selectedTagIds,
            onTagToggle = { tagId, isSelected ->
                viewModel.onTagFilterChanged(tagId, isSelected)
            },
            onDismiss = { showTagDialog = false },
            onConfirm = { showTagDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.content_desc_create_note))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text(stringResource(R.string.search_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showTagDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.tags_filter_button, uiState.selectedTagIds.size))
                }
            }

            SortOptionSelector(
                selectedOption = uiState.sortBy,
                onOptionSelected = { viewModel.onSortChanged(it) }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!.toMessage(context),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else if (uiState.notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_notes),
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notes) { note ->
                        NoteListItem(
                            note = note,
                            onNoteClick = { onNoteClick(note.id) },
                            onDelete = { viewModel.onDeleteNote(note.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteListItem(
    note: Note,
    onNoteClick: () -> Unit,
    onDelete: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onNoteClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (note.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    note.tags.take(3).forEach { tag ->
                        androidx.compose.material3.AssistChip(
                            onClick = {},
                            label = { Text(tag.name, style = androidx.compose.material3.MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    if (note.tags.size > 3) {
                        Text(
                            stringResource(R.string.more_tags, note.tags.size - 3),
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(androidx.compose.ui.Alignment.CenterVertically)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                androidx.compose.material3.TextButton(onClick = onDelete, modifier = Modifier.height(32.dp)) {
                    Text(stringResource(R.string.delete), style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
