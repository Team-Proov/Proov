package com.proov.proov.core.designsystem.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.proov.proov.core.designsystem.theme.ProovTheme

/**
 * @param dismissible false이면 뒤로 가기와 바깥 터치로 닫히지 않는다.
 * @param content null이면 본문을 그리지 않는다.
 */
@Composable
fun ProovDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: String? = null,
    dismissible: Boolean = true,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible,
            usePlatformDefaultWidth = false,
        ),
    ) {
        ProovDialogContent(
            title = title,
            content = content,
            confirmText = confirmText,
            onConfirm = onConfirm,
            modifier = modifier,
        )
    }
}

@Composable
private fun ProovDialogContent(
    title: String,
    content: String?,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title)

        content?.let { Text(text = it) }

        Button(onClick = onConfirm) {
            Text(text = confirmText)
        }
    }
}

@Preview
@Composable
private fun ProovDialogPreview() {
    ProovTheme {
        ProovDialogContent(
            title = "인터넷 연결이 끊겼어요",
            content = null,
            confirmText = "재시도",
            onConfirm = {},
        )
    }
}
