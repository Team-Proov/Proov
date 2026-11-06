# Proov 아키텍처

## 목적

이 문서는 모듈 구조, 계층 책임, 의존성 방향, 상태 관리, DI 규칙의 단일 기준이다.

## 모듈 구조

| 모듈 | 책임 |
| --- | --- |
| `androidApp` | Android 진입점(`MainActivity`)과 Android 전용 설정 |
| `iosApp` | iOS 진입점. SwiftUI 코드는 이 모듈에만 둔다 |
| `shared` | iOS Framework 우산 모듈이자 앱의 조합 지점. `commonMain`의 `ui`(`ProovRoot`, `ProovApp`, `ProovAppState`, `MainViewModel`, `MainTab`)와 `di`(`AppGraph`, ViewModel 팩토리, 이미지 로더 바인딩), 플랫폼별 그래프(`AndroidAppGraph`, `IosAppGraph`), iOS 진입점(`MainViewController`)을 둔다 |
| `presentation:*` | 기능별 화면, UiState, ViewModel, 라우트와 `NavGraphBuilder` 확장 |
| `core:*` | 여러 기능이 공유하는 모듈(`core:designsystem`, `core:navigation` 등) |
| `core:common` | 여러 모듈이 공유하는 확장 함수(`extension`)와 유틸리티(`util`). Compose와 Lifecycle의 KMP 아티팩트까지만 의존하며 `implementation`으로만 선언해 소비 모듈로 전이하지 않는다. 플랫폼 API는 쓰지 않고, 다른 `core:*` 모듈을 참조하지 않는다 |
| `core:localstorage` | 일반 설정 저장소(DataStore Preferences, KMP 공통 구현). Metro `LocalStorageProvider`가 `DataStore<Preferences>`를 제공하고, 파일 경로(`@Named(PREFERENCES_FILE)`)만 플랫폼 그래프가 제공한다. 토큰 같은 민감한 값의 보안 저장소는 범위 밖이며 로그인 설계 때 별도로 정한다 |
| `core:trigger` | 앱 공통 UI 이벤트(토스트, 스낵바, 다이얼로그). `GlobalUiEventHolder`와 `LocalGlobalUiEventTrigger`, 표시를 담당하는 `GlobalMessageHost`, 앱 공통 다이얼로그 문구를 정의한 `DialogType`을 둔다. `shared`의 `ProovApp`이 홀더를 제공하고 다이얼로그는 슬롯 하나로 표시한다. 각 화면은 사이드 이펙트를 수집해 `LocalGlobalUiEventTrigger.current`로 호출하며, `DialogType`에는 앱 공통 정책만 두고 기능 전용 다이얼로그는 해당 화면에서 `ProovDialog`를 직접 선언한다 |
| `core:network` | Ktor `HttpClient` 생성과 Metro `NetworkProvider`. 서버 base URL은 `local.properties`의 `BASE_URL`로 주입한다. `NetworkMonitor`(`isOnline: Flow<Boolean>`) 인터페이스는 공통이고, 구현체(`AndroidNetworkMonitor`, `iOSNetworkMonitor`)는 각 플랫폼 소스셋에서 `@ContributesBinding`으로 바인딩한다. 오프라인 여부는 `ProovAppState.isOffline`으로 노출한다 |

## 의존성 방향

`androidApp`/`iosApp` → `shared` → `presentation:*` → `core:*` 방향으로만 의존한다.

- `@DependencyGraph`는 `shared`의 플랫폼 소스셋(`androidMain`, `iosMain`)에만 둔다. Metro는 그래프를 소유한 모듈의 컴파일 클래스패스에서 기여(`@ContributesTo` 등)를 수집하므로, `@ContributesTo`/`@ContributesIntoMap`을 쓰는 모듈은 `shared`가 직접 의존해야 한다.
- `presentation:*`과 `core:*`는 `shared`를 참조하지 않는다. ViewModel 주입은 `metrox-viewmodel-compose`의 `metroViewModel()`만 쓴다.

- `core:*`는 `presentation:*`과 `shared`를 참조하지 않는다.
- `presentation:*` 모듈끼리는 서로 직접 참조하지 않는다. 화면 간 이동은 `shared`에서 조합한다.
- 플랫폼 API는 `commonMain`에서 직접 쓰지 않고 `expect`/`actual` 또는 인터페이스 주입으로 분리한다.

## 상태 관리

MVVM으로 구성하며 화면 상태는 `UiState`로, 사용자 동작은 ViewModel의 함수 호출로 전달한다.

- ViewModel은 화면별 `UiState`를 `StateFlow`로 노출한다.
- Compose UI는 `UiState`를 읽어 그리고 이벤트는 함수로 전달한다.
- 상태 전이는 `commonTest`에서 단위 테스트로 고정한다. 외부 의존성은 fake로 분리한다.

## 의존성 주입과 네트워크

- DI는 Metro, 네트워크는 Ktor를 사용한다.
- 각 모듈은 `@ContributesTo(AppScope::class)`로 바인딩을 제공하고, 그래프(`@DependencyGraph`)는 모든 모듈을 조합하는 `shared`에만 둔다. 의존성 누락과 순환은 컴파일 시점에 검출된다.
- 현재는 네트워크 기본 설정만 구성하며, 서버 연동 구현은 `docs/server` 명세가 작성된 뒤에 시작한다.

## 모듈 추가 규칙

- 새 모듈은 `build-logic`의 컨벤션 플러그인(`proov.kmp.library`, `proov.kmp.compose`, `proov.kmp.serialization`, `proov.kmp.feature`, 앱 모듈은 `proov.android.application`)을 적용한다. 적용은 `alias(libs.plugins.proov.kmp.library)`처럼 버전 카탈로그 별칭으로 하며, 플러그인을 추가하면 `build-logic`의 `gradlePlugin` 등록과 `libs.versions.toml`의 `[plugins]`에 함께 등록한다.
- 플러그인 클래스(진입점)는 `build-logic/convention/src/main/kotlin` 루트에 두고, 구현체(`DependencyManager`, `AppConfig`, `Catalog`)는 `com.proov.proov.buildlogic` 패키지에 `internal`로 숨긴다. 플러그인이 추가하는 의존성은 `DependencyManager`에 기능별 함수로 모은다. SDK·JVM 버전·namespace 규칙·플러그인 ID 상수는 `AppConfig.kt`에서 관리한다. JVM 타깃은 11이다.
- 모듈 간 의존은 `implementation(projects.core.model)`처럼 타입 세이프 접근자를 쓴다. 별도 빌드인 `build-logic` 안에서는 접근자를 쓸 수 없으므로, 플러그인이 추가하는 core 의존(`DependencyManager.addFeatureDependencies`)만 경로 문자열로 남긴다.
- 모듈 추가 시 `settings.gradle.kts`에 등록하고 이 문서의 모듈 표를 갱신한다.
