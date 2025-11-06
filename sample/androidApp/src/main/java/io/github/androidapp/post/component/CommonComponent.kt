package io.github.androidapp.post.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.androidapp.post.list.PostUiModel

@Composable
fun PostItem(post: PostUiModel, onItemClicked: (PostUiModel) -> Unit = {}) {
    Column(
        modifier = Modifier
            .clickable {
                onItemClicked(post)
            }
            .padding(16.dp)
    ) {
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = post.body,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2
        )
    }
}