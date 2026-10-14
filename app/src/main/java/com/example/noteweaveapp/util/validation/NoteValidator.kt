package com.example.noteweaveapp.util.validation

object NoteValidator {
    fun isValidTitle(title: String): Boolean {
        return title.trim().isNotEmpty()
    }

    fun isValidContent(content: String): Boolean {
        return content.trim().isNotEmpty()
    }

    fun validateNote(title: String, content: String): ValidationResult {
        return when {
            !isValidTitle(title) -> ValidationResult.InvalidTitle("タイトルは必須です")
            !isValidContent(content) -> ValidationResult.InvalidContent("本文は必須です")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class InvalidTitle(val message: String) : ValidationResult()
    data class InvalidContent(val message: String) : ValidationResult()
}
