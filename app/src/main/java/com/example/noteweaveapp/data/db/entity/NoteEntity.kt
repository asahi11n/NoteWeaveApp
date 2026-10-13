package com.example.noteweaveapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val summary: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false
)
