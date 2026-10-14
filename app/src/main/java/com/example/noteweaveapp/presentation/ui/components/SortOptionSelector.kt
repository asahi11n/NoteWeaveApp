package com.example.noteweaveapp.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.noteweaveapp.R
import com.example.noteweaveapp.presentation.state.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionSelector(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    val options = listOf(
        SortOption.UPDATED_DESC to stringResource(R.string.sort_newest),
        SortOption.UPDATED_ASC to stringResource(R.string.sort_oldest),
        SortOption.TITLE_ASC to stringResource(R.string.sort_title_asc)
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (option, label) ->
            SegmentedButton(
                selected = selectedOption == option,
                onClick = { onOptionSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                androidx.compose.material3.Text(label)
            }
        }
    }
}
