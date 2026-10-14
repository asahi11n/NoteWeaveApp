package com.example.noteweaveapp.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val summary: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
    val tags: List<Tag> = emptyList()
)
