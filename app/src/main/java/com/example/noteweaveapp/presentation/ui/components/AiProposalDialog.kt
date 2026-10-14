package com.example.noteweaveapp.presentation.ui.components

import androidx.compose.ui.res.stringResource
import com.example.noteweaveapp.R
import androidx.compose.ui.platform.LocalContext
import com.example.noteweaveapp.presentation.ui.toMessage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteweaveapp.presentation.state.AiProcessState

@Composable
fun AiSummaryProposalDialog(
    state: AiProcessState,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is AiProcessState.Processing -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.generating_summary)) },
                text = { CircularProgressIndicator() },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
        is AiProcessState.Success -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.summary_proposal)) },
                text = { Text(state.result) },
                confirmButton = {
                    Button(onClick = { onConfirm(state.result) }) {
                        Text(stringResource(R.string.adopt))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.reject))
                    }
                }
            )
        }
        is AiProcessState.Error -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.error_title)) },
                text = { Text(state.error.toMessage(LocalContext.current)) },
                confirmButton = {
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        else -> {}
    }
}

@Composable
fun AiTagsProposalDialog(
    state: AiProcessState,
    suggestedTags: List<String>,
    onConfirm: (List<String>) -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    val selectedTags = remember { mutableStateOf(suggestedTags.toSet()) }

    when (state) {
        is AiProcessState.Processing -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.generating_tags)) },
                text = { CircularProgressIndicator() },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
        is AiProcessState.Success -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.tag_suggestions)) },
                text = {
                    Column {
                        suggestedTags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tag in selectedTags.value,
                                    onCheckedChange = { isChecked ->
                                        selectedTags.value = if (isChecked) {
                                            selectedTags.value + tag
                                        } else {
                                            selectedTags.value - tag
                                        }
                                    }
                                )
                                Text(tag, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { onConfirm(selectedTags.value.toList()) }) {
                        Text(stringResource(R.string.adopt))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.reject))
                    }
                }
            )
        }
        is AiProcessState.Error -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(stringResource(R.string.error_title)) },
                text = { Text(state.error.toMessage(LocalContext.current)) },
                confirmButton = {
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        else -> {}
    }
}
