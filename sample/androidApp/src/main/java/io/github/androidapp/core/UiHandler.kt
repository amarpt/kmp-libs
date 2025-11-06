package io.github.androidapp.core

import androidx.compose.runtime.Composable
import io.github.postapi.core.Resource

@Composable
fun <T> Resource<T>.handleUiState(
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (String, T?) -> Unit
) {
    when (this) {
        is Resource.Loading -> onLoading()
        is Resource.Success -> onSuccess(data)
        is Resource.Error -> {
            if (message.isEmpty() && data != null)
                onSuccess(data!!)
            else
                onError(message, data)
        }
    }
}