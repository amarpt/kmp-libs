package io.github.postapi.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error<out T : Any>(
        val code: Int? = null,
        val message: String? = null,
        val exception: Throwable? = null
    ) : ApiResult<T>()
}

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val message: String, val data: T? = null) : Resource<T>()
}

inline fun <DTO : Any, DOMAIN : Any> transformAndCache(
    crossinline getCachedData: () -> Flow<DTO?>,
    crossinline fetchRemote: suspend () -> ApiResult<DTO>,
    crossinline saveRemoteData: suspend (DTO) -> Unit,
    crossinline dtoToDomain: (DTO) -> DOMAIN
): Flow<Resource<DOMAIN>> = flow {

    var cachedData: DOMAIN? = null

    getCachedData()
        .map { dto ->
            when {
                dto == null -> null
                dto is List<*> && dto.isEmpty() -> null
                else -> dto
            }
        }
        .firstOrNull()?.let {
            cachedData = dtoToDomain(it).apply {
                emit(Resource.Success(this))
            }
        }

    when (val result = fetchRemote()) {
        is ApiResult.Success -> {
            saveRemoteData(result.data)
            emit(Resource.Success(dtoToDomain(result.data)))
        }

        is ApiResult.Error -> {
            emit(Resource.Error(result.message.orEmpty(), cachedData))
        }
    }
}
    .onStart { emit(Resource.Loading) }
    .flowOn(Dispatchers.IO)