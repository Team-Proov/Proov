package com.proov.proov.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration

/**
 * 백스택을 탭 단위로 나눠 가지는 네비게이션 상태이다.
 *
 * @param startKey 앱의 시작 탭 키. 이 탭의 루트에서 뒤로 가면 앱이 종료된다.
 * @param topLevelStack 방문한 탭의 순서. 탭 키만 담는다.
 * @param subStacks 탭별 백스택. 첫 요소는 항상 그 탭의 루트 키이다.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val topLevelKeys: Set<NavKey> get() = subStacks.keys

    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey] ?: error("$currentTopLevelKey 탭의 백스택이 없다")

    /** 화면에 보이는 키. 현재 탭의 백스택 최상단이다. */
    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }
}

/**
 * 프로세스 종료와 구성 변경 뒤에도 유지되는 [NavigationState]를 만든다.
 *
 * @param configuration 모든 [NavKey] 구현체를 등록한 직렬화 설정. iOS에는 리플렉션이 없어서 등록이 필수이다.
 */
@Composable
fun rememberNavigationState(
    configuration: SavedStateConfiguration,
    startKey: NavKey,
    topLevelKeys: Set<NavKey>,
): NavigationState {
    val topLevelStack = rememberNavBackStack(configuration, startKey)
    val subStacks = topLevelKeys.associateWith { key -> rememberNavBackStack(configuration, key) }

    return remember(startKey, topLevelKeys, topLevelStack) {
        NavigationState(startKey = startKey, topLevelStack = topLevelStack, subStacks = subStacks)
    }
}

/**
 * 탭별 백스택을 하나의 [NavEntry] 목록으로 합친다.
 * 데코레이터 순서가 중요하다: 저장 상태 홀더가 먼저, ViewModel 저장소가 그다음이다.
 */
@Composable
fun NavigationState.toEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
    }

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
