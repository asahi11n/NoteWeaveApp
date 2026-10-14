package com.example.noteweaveapp.util

import com.example.noteweaveapp.domain.model.Note
import com.example.noteweaveapp.domain.model.Tag

object SampleDataGenerator {
    fun generateSampleNotes(): List<Note> {
        val now = System.currentTimeMillis()

        val androidTag = Tag(1, "Android")
        val kotlinTag = Tag(2, "Kotlin")
        val composeTag = Tag(3, "Compose")
        val roomTag = Tag(4, "Room")
        val viewmodelTag = Tag(5, "ViewModel")

        return listOf(
            Note(
                id = 1,
                title = "Android Architecture Guide",
                content = "Understanding MVVM and Repository patterns in Android development. This pattern separates UI from business logic.",
                createdAt = now - 86400000,
                updatedAt = now - 86400000,
                tags = listOf(androidTag, kotlinTag)
            ),
            Note(
                id = 2,
                title = "Jetpack Compose Basics",
                content = "Compose is a modern toolkit for building Android UIs with Kotlin. It makes UI development faster and easier.",
                createdAt = now - 172800000,
                updatedAt = now - 172800000,
                tags = listOf(composeTag, androidTag, kotlinTag)
            ),
            Note(
                id = 3,
                title = "Room Database Setup",
                content = "Room provides an abstraction layer over SQLite. It handles database migrations and provides compile-time verification.",
                createdAt = now - 259200000,
                updatedAt = now - 259200000,
                tags = listOf(roomTag, androidTag)
            ),
            Note(
                id = 4,
                title = "ViewModel Lifecycle",
                content = "ViewModels survive configuration changes and are responsible for managing UI state in Android applications.",
                createdAt = now - 345600000,
                updatedAt = now - 345600000,
                tags = listOf(viewmodelTag, androidTag)
            ),
            Note(
                id = 5,
                title = "Kotlin Coroutines",
                content = "Coroutines provide a way to write asynchronous code in Kotlin. They are lightweight and efficient for concurrent operations.",
                createdAt = now - 432000000,
                updatedAt = now - 432000000,
                tags = listOf(kotlinTag)
            ),
            Note(
                id = 6,
                title = "StateFlow and Flow",
                content = "StateFlow and Flow are reactive types in Kotlin that emit values over time. Great for managing state in reactive systems.",
                createdAt = now - 518400000,
                updatedAt = now - 518400000,
                tags = listOf(kotlinTag, composeTag)
            )
        )
    }

    fun generateSampleTags(): List<Tag> {
        return listOf(
            Tag(1, "Android"),
            Tag(2, "Kotlin"),
            Tag(3, "Compose"),
            Tag(4, "Room"),
            Tag(5, "ViewModel"),
            Tag(6, "Architecture"),
            Tag(7, "Testing"),
            Tag(8, "Performance")
        )
    }
}
