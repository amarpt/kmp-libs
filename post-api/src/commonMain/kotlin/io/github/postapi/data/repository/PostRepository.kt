package io.github.postapi.data.repository

import io.github.postapi.data.remote.PostApiClient
import io.github.postapi.core.Resource
import io.github.postapi.core.transformAndCache
import io.github.postapi.data.storage.PostStorage
import io.github.postapi.domain.model.Post
import io.github.postapi.domain.model.PostRepository
import io.github.postapi.domain.mapper.toDomain
import kotlinx.coroutines.flow.Flow

internal class PostRepositoryImpl(
    private val postStorage: PostStorage,
    private val postApiClient: PostApiClient
) : PostRepository {

    override fun getAllPost(): Flow<Resource<List<Post>>> = transformAndCache(
        getCachedData = { postStorage.getAll() },
        fetchRemote = { postApiClient.fetchPosts() },
        saveRemoteData = { postStorage.saveAll(it) },
        dtoToDomain = { it.map { it.toDomain() } }
    )

    override fun getPostById(id: Int): Flow<Resource<Post>> = transformAndCache(
        getCachedData = { postStorage.get(id) },
        fetchRemote = { postApiClient.fetchPost(id) },
        saveRemoteData = { postStorage.save(it) },
        dtoToDomain = { it.toDomain() })
}