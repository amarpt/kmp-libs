package io.github.postapi.data.storage

import io.github.postapi.data.model.PostDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface PostStorage {
    suspend fun saveAll(post: List<PostDTO>)
    suspend fun save(post: PostDTO)
    fun get(id: Int): Flow<PostDTO?>
    fun getAll(): Flow<List<PostDTO>>
    fun clearAll()
}

internal class PostStorageImpl() : PostStorage {
    private val storePosts = MutableStateFlow(emptyList<PostDTO>())

    override suspend fun saveAll(post: List<PostDTO>) {
        storePosts.value = post
    }

    override suspend fun save(post: PostDTO) {
        storePosts.update { posts ->
            posts
                .filterNot { it.id == post.id }
                .plus(post)
        }
    }

    override fun get(id: Int): Flow<PostDTO?> =
        storePosts.map { posts ->
            posts.find { it.id == id }
        }

    override fun clearAll() {
        storePosts.value = emptyList()
    }

    override fun getAll(): Flow<List<PostDTO>> = storePosts
}