package com.example.noteweaveapp.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteweaveapp.presentation.state.SortOption

@Composable
fun SortOptionSelector(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    Row(modifier = Modifier.padding(8.dp)) {
        listOf(
            SortOption.UPDATED_DESC to "Newest",
            SortOption.UPDATED_ASC to "Oldest",
            SortOption.TITLE_ASC to "Title A-Z"
        ).forEach { (option, label) ->
            Row(modifier = Modifier.padding(end = 16.dp)) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(label, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}
