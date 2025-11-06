package io.github.postapi.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <UI, DOMAIN> Flow<Resource<DOMAIN>>.mapResource(
    transform: (DOMAIN) -> UI
): Flow<Resource<UI>> = map { resource ->
    when (resource) {
        is Resource.Success -> Resource.Success(transform(resource.data))
        is Resource.Error -> Resource.Error(resource.message, resource.data?.let(transform))
        is Resource.Loading -> Resource.Loading
    }
}