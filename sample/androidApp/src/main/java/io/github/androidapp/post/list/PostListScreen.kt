package io.github.androidapp.post.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.androidapp.R
import io.github.androidapp.post.component.PostItem
import io.github.androidapp.post.detail.PostDetailSheet
import io.github.postapi.core.Resource

@Composable
fun PostListScreen(viewModel: PostListViewModel = hiltViewModel<PostListViewModel>()) {
    val uiState by viewModel.postUiState.collectAsStateWithLifecycle(Resource.Loading)
    var selectedPost by remember { mutableStateOf<PostUiModel?>(null) }

    PostListScreen(
        uiState,
        onRetry = viewModel::refresh,
        onItemClicked = {
            selectedPost = it
        })

    selectedPost?.let {
        PostDetailSheet(it) { selectedPost = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    uiState: PostUiState,
    onItemClicked: (PostUiModel) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.latest_posts)) }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is Resource.Loading -> PostLoading()
                    is Resource.Success -> PostListContent(uiState.data, onItemClicked)
                    is Resource.Error -> {
                        if (uiState.message.isEmpty())
                            PostListContent(uiState.data!!, onItemClicked)
                        else
                            PostError(uiState.message, onRetry)
                    }
                }

//                uiState.handleUiState(
//                    onLoading = { PostLoading() },
//                    onSuccess = { PostListContent(it, onItemClicked) },
//                    onError = { message, _ -> PostError(message, onRetry) }
//                )
            }
        }
    )
}

@Composable
fun PostListContent(posts: List<PostUiModel>, onItemClicked: (PostUiModel) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(items = posts, key = { it.id }) { post ->
            PostItem(post, onItemClicked)
            HorizontalDivider()
        }
    }
}

@Composable
fun PostError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, modifier = Modifier.padding(16.dp))
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onRetry() }) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun PostLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}