package com.proov.proov.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * 화면 이동의 유일한 진입점이다. feature는 백스택을 직접 조작하지 않고 이 클래스를 통해서만 이동한다.
 */
class Navigator(val state: NavigationState) {

    /** 현재 탭의 키이면 탭 루트까지 되돌리고, 다른 탭의 키이면 탭을 전환하며, 그 외에는 현재 탭에 쌓는다. */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> popToRoot()
            in state.topLevelKeys -> switchTab(key)
            else -> push(key)
        }
    }

    /** 시작 탭의 루트에서는 아무 일도 하지 않는다. 이전 화면이 없으면 시스템이 앱을 종료한다. */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> Unit
            state.currentTopLevelKey -> state.topLevelStack.removeLastOrNull()
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    private fun push(key: NavKey) {
        state.currentSubStack.apply {
            // 이미 있으면 맨 위로 옮겨 같은 화면이 중복되지 않게 한다
            remove(key)
            add(key)
        }
    }

    private fun switchTab(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) clear() else remove(key)
            add(key)
        }
    }

    private fun popToRoot() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}
