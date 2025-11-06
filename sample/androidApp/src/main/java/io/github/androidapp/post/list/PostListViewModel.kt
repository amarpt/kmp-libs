package io.github.androidapp.post.list

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.postapi.core.mapResource
import io.github.postapi.core.Resource
import io.github.postapi.domain.model.Post
import io.github.postapi.domain.model.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias PostUiState = Resource<List<PostUiModel>>

@Stable
data class PostUiModel(
    var userId: Int,
    var id: Int,
    var title: String,
    var body: String
)

@HiltViewModel
class PostListViewModel @Inject constructor(postRepository: PostRepository) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val postUiState: Flow<PostUiState> =
        refreshTrigger.flatMapLatest {
            delay(500) // Extra network delay
            postRepository.getAllPost()
        }
            .mapResource { posts -> posts.map { it.toUiModel() } }
            .stateIn(
                viewModelScope,
                WhileSubscribed(5000),
                Resource.Loading
            )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}

fun Post.toUiModel() = PostUiModel(
    userId = userId,
    id = id,
    title = title,
    body = body
)