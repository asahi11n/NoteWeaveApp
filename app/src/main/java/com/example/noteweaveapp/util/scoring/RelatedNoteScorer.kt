package com.example.noteweaveapp.util.scoring

import com.example.noteweaveapp.domain.model.Note

object RelatedNoteScorer {
    data class ScoredNote(
        val note: Note,
        val score: Int
    )

    fun scoreRelatedNotes(currentNote: Note, allNotes: List<Note>, limit: Int = 5): List<Note> {
        if (currentNote.tags.isEmpty()) {
            return emptyList()
        }

        val currentTagIds = currentNote.tags.map { it.id }.toSet()

        return allNotes
            .filter { it.id != currentNote.id }
            .map { note ->
                val noteTagIds = note.tags.map { it.id }.toSet()
                val commonTagCount = currentTagIds.intersect(noteTagIds).size
                val score = commonTagCount * 10

                ScoredNote(note, score)
            }
            .filter { it.score > 0 }
            .sortedByDescending { it.score }
            .take(limit)
            .map { it.note }
    }

    fun calculateSimilarityScore(note1: Note, note2: Note): Int {
        val tags1 = note1.tags.map { it.id }.toSet()
        val tags2 = note2.tags.map { it.id }.toSet()
        val commonCount = tags1.intersect(tags2).size

        return commonCount * 10
    }
}
