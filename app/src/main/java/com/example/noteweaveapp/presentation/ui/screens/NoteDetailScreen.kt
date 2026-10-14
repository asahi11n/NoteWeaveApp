package com.example.noteweaveapp.presentation.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.noteweaveapp.R
import androidx.compose.ui.platform.LocalContext
import com.example.noteweaveapp.presentation.ui.toMessage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteweaveapp.presentation.state.NoteDetailUiEvent
import com.example.noteweaveapp.presentation.state.NoteDetailUiState
import com.example.noteweaveapp.presentation.ui.components.AiSummaryProposalDialog
import com.example.noteweaveapp.presentation.ui.components.AiTagsProposalDialog
import com.example.noteweaveapp.presentation.viewmodel.NoteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSummaryDialog by remember { mutableStateOf(false) }
    var showTagsDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteDetailUiEvent.ShowError -> snackbarHostState.showSnackbar(event.error.toMessage(context))
            }
        }
    }

    if (showSummaryDialog && uiState is NoteDetailUiState.Success) {
        AiSummaryProposalDialog(
            state = (uiState as NoteDetailUiState.Success).aiSummaryState,
            onConfirm = { summary ->
                viewModel.onSummaryAdopted(summary)
                showSummaryDialog = false
            },
            onCancel = {
                viewModel.onSummaryRejected()
                showSummaryDialog = false
            },
            onRetry = { viewModel.onSummaryGenerate() }
        )
    }

    if (showTagsDialog && uiState is NoteDetailUiState.Success) {
        AiTagsProposalDialog(
            state = (uiState as NoteDetailUiState.Success).aiTagsState,
            suggestedTags = (uiState as NoteDetailUiState.Success).suggestedTags,
            onConfirm = { tags ->
                viewModel.onTagsAdopted(tags)
                showTagsDialog = false
            },
            onCancel = {
                viewModel.onTagsRejected()
                showTagsDialog = false
            },
            onRetry = { viewModel.onTagsGenerate() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.note_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_desc_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is NoteDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is NoteDetailUiState.Error -> {
                    Text(
                        text = state.error.toMessage(context),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
                is NoteDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = state.note.title,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = state.note.content,
                            modifier = Modifier.padding(top = 16.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )

                        if (state.note.tags.isNotEmpty()) {
                            Text(
                                text = stringResource(
                                    R.string.note_tags_label,
                                    state.note.tags.joinToString(", ") { it.name }
                                ),
                                modifier = Modifier.padding(top = 16.dp),
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                            )
                        }

                        if (state.relatedNotes.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.related_notes),
                                modifier = Modifier.padding(top = 24.dp),
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                            )
                            state.relatedNotes.forEach { relatedNote ->
                                androidx.compose.material3.Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    onClick = { onBackClick() }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = relatedNote.title,
                                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = relatedNote.content.take(60) + "...",
                                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { onEditClick(state.note.id) }) {
                                Text(stringResource(R.string.edit))
                            }
                            Button(onClick = {
                                viewModel.onSummaryGenerate()
                                showSummaryDialog = true
                            }) {
                                Text(stringResource(R.string.summary))
                            }
                            Button(onClick = {
                                viewModel.onTagsGenerate()
                                showTagsDialog = true
                            }) {
                                Text(stringResource(R.string.tags_label))
                            }
                        }
                    }
                }
            }
        }
    }
}
