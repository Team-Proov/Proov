package com.kindl.proov.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kindl.proov.core.designsystem.ProovTheme

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
