package io.github.postapi.domain.model

import io.github.postapi.core.Resource
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPost(): Flow<Resource<List<Post>>>
    fun getPostById(id: Int): Flow<Resource<Post>>
}