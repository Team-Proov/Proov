package com.proov.proov.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Navigation 3 백스택에 쌓이는 모든 라우트의 마커이다. 구현체는 `@Serializable`이어야 한다.
 * 새 라우트는 `shared`의 `ProovNavConfig`에 등록해야 하며, 등록하지 않으면 iOS에서 백스택 복원이 실패한다.
 * 자세한 규칙은 `docs/architecture/navigation.md`를 따른다.
 */
interface Route : NavKey

/** 바텀 탭에 노출되는 최상위 라우트의 마커. 이 라우트에서만 바텀 바를 보여준다. */
interface MainRouteTab : Route
