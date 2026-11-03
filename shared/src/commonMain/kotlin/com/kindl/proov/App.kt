package com.kindl.proov

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kindl.proov.core.designsystem.Proov
import com.kindl.proov.core.designsystem.ProovTheme

@Composable
@Preview
fun App() {
    ProovTheme {
        Box(
            modifier = Modifier
                .background(Proov.colors.bg)
                .safeContentPadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Proov", style = Proov.typography.h1, color = Proov.colors.ink)
        }
    }
}
