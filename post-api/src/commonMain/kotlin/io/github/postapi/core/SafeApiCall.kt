package io.github.postapi.core

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

private suspend inline fun <reified T : Any> safeApiCall(apiCall: suspend () -> HttpResponse): ApiResult<T> {
    return try {
        val response = apiCall()
        when (response.status.value) {
            in 200..299 -> {
                val data: T = response.body()
                ApiResult.Success(data)
            }

            else -> {
                ApiResult.Error(
                    code = response.status.value,
                    message = response.status.description
                )
            }
        }
    } catch (e: Exception) {
        ApiResult.Error(message = e.message, exception = e)
    }
}

internal suspend inline fun <reified T : Any> HttpClient.safeCall(url: String): ApiResult<T> =
    safeApiCall { this.get(url) }