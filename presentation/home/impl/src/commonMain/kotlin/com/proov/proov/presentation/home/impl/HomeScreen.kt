package com.proov.proov.presentation.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.proov.proov.core.designsystem.theme.ProovTheme

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
private fun HomeScreen() {
}

@Composable
@Preview
private fun HomeScreenPreview() {
    ProovTheme {
        HomeScreen()
    }
}
