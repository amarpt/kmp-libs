package io.github.postapi.data.remote

import io.github.postapi.BuildKonfig
import io.github.postapi.core.ApiResult
import io.github.postapi.core.safeCall
import io.github.postapi.data.model.PostDTO
import io.ktor.client.HttpClient

interface PostApiClient {
    suspend fun fetchPosts(): ApiResult<List<PostDTO>>
    suspend fun fetchPost(id: Int): ApiResult<PostDTO>
}

internal class PostApiClientImpl(
    private val client: HttpClient,
    private val baseUrl: String = BuildKonfig.BASE_URL
) : PostApiClient {

    override suspend fun fetchPosts(): ApiResult<List<PostDTO>> =
        client.safeCall("$baseUrl/posts")

    override suspend fun fetchPost(id: Int): ApiResult<PostDTO> =
        client.safeCall("$baseUrl/posts/$id")
}

