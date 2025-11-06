package io.github.postapi.core

import kotlinx.coroutines.flow.Flow

fun <T : Any> Flow<Resource<T>>.collectResult(
    onSuccess: (T) -> Unit,
    onLoading: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val collector = IOSFlowCollector<Resource<T>>()
    collector.collect(
        flow = this,
        onValue = { result ->
            when (result) {
                is Resource.Success -> onSuccess(result.data)
                is Resource.Loading -> onLoading()
                is Resource.Error -> onError(result.message)
            }
        }
    )
}