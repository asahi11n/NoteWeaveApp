package com.example.noteweaveapp.util.scoring

import com.example.noteweaveapp.domain.model.Note

object RelatedNoteScorer {
    fun scoreRelatedNotes(currentNote: Note, allNotes: List<Note>, limit: Int = 5): List<Note> {
        if (currentNote.tags.isEmpty()) return emptyList()
        val currentTagIds = currentNote.tags.map { it.id }.toSet()
        return allNotes.filter { it.id != currentNote.id }
            .map { note ->
                val noteTagIds = note.tags.map { it.id }.toSet()
                Pair(note, currentTagIds.intersect(noteTagIds).size * 10)
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    fun calculateSimilarityScore(note1: Note, note2: Note): Int {
        val tags1 = note1.tags.map { it.id }.toSet()
        val tags2 = note2.tags.map { it.id }.toSet()
        return tags1.intersect(tags2).size * 10
    }
}
