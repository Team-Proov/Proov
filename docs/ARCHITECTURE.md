# Proov 아키텍처

## 목적

이 문서는 모듈 구조, 계층 책임, 의존성 방향, 상태 관리, DI 규칙의 단일 기준이다.

## 모듈 구조

| 모듈 | 책임 |
| --- | --- |
| `androidApp` | Android 진입점(`MainActivity`)과 Android 전용 설정 |
| `iosApp` | iOS 진입점. SwiftUI 코드는 이 모듈에만 둔다 |
| `shared` | 앱 루트(`App`)와 각 모듈을 조합하는 공통 코드 |
| `presentation:*` | 기능별 화면, UiState, ViewModel |
| `core:*` | 여러 기능이 공유하는 모듈(`core:designsystem` 등) |

## 의존성 방향

`androidApp`/`iosApp` → `shared` → `presentation:*` → `core:*` 방향으로만 의존한다.

- `core:*`는 `presentation:*`과 `shared`를 참조하지 않는다.
- `presentation:*` 모듈끼리는 서로 직접 참조하지 않는다. 화면 간 이동은 `shared`에서 조합한다.
- 플랫폼 API는 `commonMain`에서 직접 쓰지 않고 `expect`/`actual` 또는 인터페이스 주입으로 분리한다.

## 상태 관리

MVVM으로 구성하며 화면 상태는 `UiState`로, 사용자 동작은 ViewModel의 함수 호출로 전달한다.

- ViewModel은 화면별 `UiState`를 `StateFlow`로 노출한다.
- Compose UI는 `UiState`를 읽어 그리고 이벤트는 함수로 전달한다.
- 상태 전이는 `commonTest`에서 단위 테스트로 고정한다. 외부 의존성은 fake로 분리한다.

## 의존성 주입과 네트워크

- DI는 Koin, 네트워크는 Ktor를 사용한다.
- 현재는 네트워크 기본 설정만 구성하며, 서버 연동 구현은 `docs/server` 명세가 작성된 뒤에 시작한다.

## 모듈 추가 규칙

- 새 모듈은 `build-logic`의 컨벤션 플러그인(`KmpLibraryConventionPlugin`, `KmpFeatureConventionPlugin`, `KmpComposeConventionPlugin`)을 적용한다.
- 모듈 추가 시 `settings.gradle.kts`에 등록하고 이 문서의 모듈 표를 갱신한다.
