package com.example.noteweaveapp.presentation.ui

import android.content.Context
import com.example.noteweaveapp.R
import com.example.noteweaveapp.presentation.state.FailedAction
import com.example.noteweaveapp.presentation.state.UiError

fun UiError.toMessage(context: Context): String = when (this) {
    UiError.NoteNotFound -> context.getString(R.string.error_note_not_found)
    is UiError.TagTooLong -> context.getString(R.string.error_tag_too_long, maxLength)
    UiError.TagEmpty -> context.getString(R.string.error_tag_empty)
    UiError.TagExists -> context.getString(R.string.error_tag_exists)
    is UiError.Failed -> context.getString(
        action.messageRes(),
        reason ?: context.getString(R.string.error_unknown)
    )
}

private fun FailedAction.messageRes(): Int = when (this) {
    FailedAction.LOAD_NOTES -> R.string.error_load_notes
    FailedAction.LOAD_NOTE -> R.string.error_load_note
    FailedAction.LOAD_TAGS -> R.string.error_load_tags
    FailedAction.DELETE_NOTE -> R.string.error_delete_note
    FailedAction.SAVE_NOTE -> R.string.error_save_note
    FailedAction.SAVE_TAGS -> R.string.error_save_tags
    FailedAction.CREATE_TAG -> R.string.error_create_tag
    FailedAction.GENERATE_SUMMARY, FailedAction.GENERATE_TAGS -> R.string.error_plain
}
