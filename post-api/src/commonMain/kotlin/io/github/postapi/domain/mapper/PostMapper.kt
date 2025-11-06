package io.github.postapi.domain.mapper

import io.github.postapi.data.model.PostDTO
import io.github.postapi.domain.model.Post

fun PostDTO.toDomain() = Post(
    userId = userId ?: 0,
    id = id ?: 0,
    title = title.orEmpty(),
    body = body.orEmpty()
)