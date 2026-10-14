package com.example.noteweaveapp.presentation.state

import com.example.noteweaveapp.domain.model.Tag

// ViewModel はエラーを型で持ち、表示用の文言は画面側で strings.xml から作る
sealed interface UiError {
    data object NoteNotFound : UiError

    data class TagTooLong(val maxLength: Int = Tag.MAX_LENGTH) : UiError

    // reason は例外のメッセージ（取得できない場合は null）
    data class Failed(val action: FailedAction, val reason: String? = null) : UiError
}

enum class FailedAction {
    LOAD_NOTES,
    LOAD_NOTE,
    LOAD_TAGS,
    DELETE_NOTE,
    SAVE_NOTE,
    SAVE_TAGS,
    GENERATE_SUMMARY,
    GENERATE_TAGS
}
