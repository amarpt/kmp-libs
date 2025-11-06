package io.github.androidapp.post.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.androidapp.post.list.PostUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailSheet(post: PostUiModel, onDismiss: () -> Unit = {}) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        PostDetail(post)
    }
}

@Composable
fun PostDetail(post: PostUiModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = post.body,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}