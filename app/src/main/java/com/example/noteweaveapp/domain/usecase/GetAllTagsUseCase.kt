package com.example.noteweaveapp.domain.usecase

import javax.inject.Inject
import com.example.noteweaveapp.data.repository.TagRepository
import com.example.noteweaveapp.domain.model.Tag
import kotlinx.coroutines.flow.Flow

class GetAllTagsUseCase @Inject constructor(private val tagRepository: TagRepository) {
    operator fun invoke(): Flow<List<Tag>> = tagRepository.getAllTagsFlow()
}
