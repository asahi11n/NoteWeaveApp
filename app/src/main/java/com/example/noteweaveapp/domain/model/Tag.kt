package com.example.noteweaveapp.domain.model

data class Tag(
    val id: Long = 0,
    val name: String
) {
    companion object {
        const val MAX_LENGTH = 30

        fun trimmed(name: String): String = name.trim()
    }
}
