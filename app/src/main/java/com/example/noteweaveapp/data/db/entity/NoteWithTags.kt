package com.example.noteweaveapp.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithTags(
    @Embedded
    val note: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Relation.Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "noteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity> = emptyList()
)
