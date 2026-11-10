package com.proov.proov.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals

private object TabA : NavKey
private object TabB : NavKey
private object DetailA : NavKey
private object DetailB : NavKey

class NavigatorTest {
    private val state = NavigationState(
        startKey = TabA,
        topLevelStack = NavBackStack<NavKey>(TabA),
        subStacks = listOf(TabA, TabB).associateWith { NavBackStack<NavKey>(it) },
    )
    private val navigator = Navigator(state)

    @Test
    fun 일반_키는_현재_탭에_쌓이고_중복되지_않는다() {
        navigator.navigate(DetailA)
        navigator.navigate(DetailA)

        assertEquals(listOf(TabA, DetailA), state.currentSubStack.toList())
    }

    @Test
    fun 탭을_전환해도_이전_탭의_백스택이_유지된다() {
        navigator.navigate(DetailA)
        navigator.navigate(TabB)
        navigator.navigate(DetailB)
        navigator.navigate(TabA)

        assertEquals(TabA, state.currentTopLevelKey)
        assertEquals(DetailA, state.currentKey)
        assertEquals(listOf(TabB, DetailB), state.subStacks.getValue(TabB).toList())
    }

    @Test
    fun 현재_탭을_다시_선택하면_탭_루트로_돌아간다() {
        navigator.navigate(DetailA)
        navigator.navigate(TabA)

        assertEquals(TabA, state.currentKey)
    }

    @Test
    fun 뒤로_가면_현재_탭의_이전_화면으로_간다() {
        navigator.navigate(DetailA)
        navigator.goBack()

        assertEquals(TabA, state.currentKey)
    }

    @Test
    fun 시작이_아닌_탭의_루트에서_뒤로_가면_이전_탭으로_간다() {
        navigator.navigate(TabB)
        navigator.goBack()

        assertEquals(TabA, state.currentTopLevelKey)
    }

    @Test
    fun 시작_탭의_루트에서는_뒤로_가도_상태가_바뀌지_않는다() {
        navigator.goBack()

        assertEquals(TabA, state.currentKey)
        assertEquals(listOf<NavKey>(TabA), state.topLevelStack.toList())
    }
}
