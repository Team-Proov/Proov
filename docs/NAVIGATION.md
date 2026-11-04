# Proov 화면 네비게이션

## 방식

Navigation 2 type-safe 방식(JetBrains `navigation-compose`)을 쓴다. 라우트는 `@Serializable` 객체·클래스이고 화면은 `composable<Route> { }`로 등록한다.

## 라우트 마커

`core:navigation`의 마커 인터페이스로 라우트 종류를 구분한다.

| 인터페이스 | 의미 |
| --- | --- |
| `Route` | 모든 type-safe 라우트 |
| `MainRouteTab : Route` | 바텀 탭에 노출되는 최상위 라우트. 이 라우트에서만 바텀 바를 보여준다 |

## 구조 규칙

- 라우트와 `fun NavGraphBuilder.xxxNavGraph(...)` 확장 함수는 해당 `presentation:*` 모듈 안의 `navigation` 패키지에 둔다. 예: `Home`, `homeNavGraph()`
- `presentation:*` 모듈끼리는 서로 참조하지 않는다. 다른 기능으로 이동할 일은 `NavGraphBuilder` 확장의 콜백 인자로 의도만 밖에 알린다.
- 콜백을 실제 `navController.navigate(...)`에 연결하는 일과 `NavHost` 조립은 `shared`의 `ui` 패키지에 있는 `ProovApp`이 맡는다.
- 바텀 탭 목록(`MainTab`)은 `shared`의 `ui.navigation` 패키지에서 관리한다. 탭이 추가되면 `MainRouteTab` 라우트를 등록한다.

## 범위 밖

딥링크와 외부 `Intent`(카카오 로그인 콜백 등) 전달 경로는 첫 소비자가 생길 때 함께 설계한다.
