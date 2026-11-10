package com.proov.proov.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.proov.proov.core.designsystem.component.dialog.ProovDialog
import com.proov.proov.core.designsystem.theme.Proov
import com.proov.proov.core.navigation.toEntries
import com.proov.proov.core.trigger.LocalGlobalUiEventTrigger
import com.proov.proov.core.trigger.model.DialogState
import com.proov.proov.core.trigger.model.DialogType
import com.proov.proov.core.trigger.ui.GlobalMessageHost
import com.proov.proov.core.trigger.ui.rememberDialogStateHolder
import com.proov.proov.core.trigger.ui.rememberGlobalMessageState
import com.proov.proov.core.trigger.ui.rememberGlobalUiEventHolder
import com.proov.proov.presentation.home.impl.homeEntry
import com.proov.proov.ui.navigation.MainTab
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ProovApp(appState: ProovAppState, viewModel: MainViewModel = metroViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState is MainUiState.Loading) return

    // 화면에 보이는 키가 탭 루트일 때만 currentTab이 null이 아니므로 바텀 바가 보인다
    val currentTab = appState.currentTab

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val messageState = rememberGlobalMessageState()
    val dialogStateHolder = rememberDialogStateHolder()
    val globalUiEventHolder = rememberGlobalUiEventHolder(messageState, dialogStateHolder)

    CompositionLocalProvider(
        LocalGlobalUiEventTrigger provides globalUiEventHolder,
    ) {
        Scaffold(
            containerColor = Proov.colors.neutral.n50,
            snackbarHost = { GlobalMessageHost(messageState) },
            bottomBar = {
                ProovBottomBar(
                    isVisible = currentTab != null,
                    tabs = appState.tabs,
                    currentTab = currentTab,
                    onTabClick = appState::navigateToTab,
                )
            },
        ) { padding ->
            NavDisplay(
                entries = appState.navigationState.toEntries(
                    entryProvider {
                        homeEntry(appState.navigator)
                    },
                ),
                onBack = appState.navigator::goBack,
                modifier = Modifier.padding(padding),
            )
        }

        ProovDialogSlot(
            isOffline = isOffline,
            dialogState = dialogStateHolder.dialogState,
            onDismiss = dialogStateHolder::dismissDialog,
            onShowToast = globalUiEventHolder.showToast,
        )
    }
}

/** 앱에서 다이얼로그는 하나만 뜬다. 오프라인이면 다른 다이얼로그보다 네트워크 다이얼로그를 우선한다. */
@Composable
private fun ProovDialogSlot(
    isOffline: Boolean,
    dialogState: DialogState,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    val type = if (isOffline) DialogType.NETWORK else dialogState.type ?: return

    ProovDialog(
        title = type.title,
        content = type.content,
        confirmText = type.confirmText,
        dismissible = type.dismissible,
        onConfirm = {
            if (isOffline) {
                onShowToast("네트워크가 아직 연결되지 않았어요")
            } else {
                dialogState.onConfirm()
                onDismiss()
            }
        },
        onDismissRequest = { if (!isOffline) onDismiss() },
    )
}

@Composable
private fun ProovBottomBar(
    isVisible: Boolean,
    tabs: ImmutableList<MainTab>,
    currentTab: MainTab?,
    onTabClick: (MainTab) -> Unit,
) {
    if (!isVisible) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Proov.colors.neutral.n0)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
    ) {
        tabs.forEach { tab ->
            // 아이콘이 생기기 전까지 contentDescription을 라벨로 대신 쓴다
            Text(
                text = tab.contentDescription,
                style = Proov.typography.caption.c12,
                color = if (tab == currentTab) Proov.colors.neutral.n900 else Proov.colors.neutral.n500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabClick(tab) },
            )
        }
    }
}
