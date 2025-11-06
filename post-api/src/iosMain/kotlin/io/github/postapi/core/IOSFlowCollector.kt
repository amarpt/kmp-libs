package io.github.postapi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IOSFlowCollector<T> {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun collect(
        flow: Flow<T>,
        onValue: (T) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        scope.launch {
            try {
                flow.collect { value ->
                    withContext(Dispatchers.Main) {
                        onValue(value)  // Safe for UI updates now
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}