package com.mantapp.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MantappEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun MantappLoadingState(
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        label?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun MantappErrorState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    FeedbackSurface(
        title = title,
        message = message,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier,
    )
}

@Composable
fun MantappSuccessMessage(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    FeedbackSurface(
        title = title,
        message = message,
        containerColor = Color(0xFFDFF5E8),
        contentColor = Color(0xFF123820),
        modifier = modifier,
    )
}

@Composable
fun MantappWarningMessage(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    FeedbackSurface(
        title = title,
        message = message,
        containerColor = Color(0xFFFFF3D6),
        contentColor = Color(0xFF4A3200),
        modifier = modifier,
    )
}

@Composable
fun MantappConfirmationMessage(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    FeedbackSurface(
        title = title,
        message = message,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier,
    )
}

@Composable
private fun FeedbackSurface(
    title: String,
    message: String?,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            message?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
