package com.example.noteweaveapp.domain.model

data class Tag(
    val id: Long = 0,
    val name: String
) {
    companion object {
        fun trimmed(name: String): String = name.trim()
    }
}
