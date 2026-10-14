package com.example.noteweaveapp.presentation.ui.components

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
                title = { Text("Generating Summary...") },
                text = { CircularProgressIndicator() },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                }
            )
        }
        is AiProcessState.Success -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text("Summary Proposal") },
                text = { Text(state.result) },
                confirmButton = {
                    Button(onClick = { onConfirm(state.result) }) {
                        Text("Adopt")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text("Reject")
                    }
                }
            )
        }
        is AiProcessState.Error -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
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
                title = { Text("Generating Tag Suggestions...") },
                text = { CircularProgressIndicator() },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                }
            )
        }
        is AiProcessState.Success -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text("Tag Suggestions") },
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
                        Text("Adopt")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text("Reject")
                    }
                }
            )
        }
        is AiProcessState.Error -> {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            )
        }
        else -> {}
    }
}
