package com.proov.proov.core.navigation

/** 모든 type-safe 라우트의 마커. 구현체는 `@Serializable`이어야 한다. */
interface Route

/** 바텀 탭에 노출되는 최상위 라우트의 마커. 이 라우트에서만 바텀 바를 보여준다. */
interface MainRouteTab : Route
