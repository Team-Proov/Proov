# Proov CMP 에이전트 가이드

## 목표와 범위

Proov는 Prove + Move의 합성어로, 목표를 말로만 끝내지 않고 공약을 만들어 인증으로 달성률을 쌓아가는 행동 기반 목표 Compose Multiplatform 앱이다. 말로만 하고 목표를 끝내지 못하던 사람을 위해 만들며, Android와 iOS를 모두 대상으로 한다.

- 이 문서는 프로젝트에 적용되는 작업 규칙이다. 하위 디렉터리에 `AGENTS.md`가 있으면 가장 가까운 지침이 우선한다.
- 아직 결정되지 않은 사항은 구현 시점에 사용자에게 결정받으며, 에이전트가 임의로 정하지 않는다.
- 아래 문서 중 "작성 예정"으로 표시된 문서는 해당 영역이 생기는 시점에 작성한다. 작성된 뒤에는 해당 영역 작업 전에 먼저 읽고 따른다.

## 아키텍처

Kotlin·Compose 코드 구현과 변경 리뷰, 모듈 구조, 계층 책임, 의존성 방향, 상태 관리, DI 설계 작업은 먼저 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)를 읽고 따른다. 이 문서와 아키텍처 문서가 충돌하면 이 문서가 우선한다.

## 하네스

구현, 디버깅, 테스트, 변경 리뷰 작업은 먼저 [docs/HARNESS.md](docs/HARNESS.md)의 작업 루프와 검증 게이트를 따른다.

## 디자인

색상, 타이포그래피, 테마를 사용하거나 변경할 때는 [DESIGN.md](DESIGN.md)의 토큰을 단일 기준으로 따른다. 색상과 글꼴은 `core:designsystem`의 `ProovTheme`, `ProovColors`, `ProovTypography`를 사용하고 임의의 값을 직접 쓰지 않는다. Figma 화면과 코드의 매핑은 [docs/FIGMA-MAP.md](docs/FIGMA-MAP.md)(작성 예정)에 기록한다.

## 화면 네비게이션

화면 이동과 탭 구조를 구현·변경할 때는 [docs/NAVIGATION.md](docs/NAVIGATION.md)(작성 예정)를 먼저 읽고 따른다.

## 서버 연동 명세

서버 연동을 구현·변경·검증할 때는 [docs/server](docs/server/)(작성 예정)의 API 명세에서 요청·응답, 인증, 오류 코드를 확인하고 이를 계약의 기준으로 따른다. 명세가 불명확하거나 충돌하면 근거를 확인하고 필요한 사항을 사용자에게 결정받는다.

## 기기 호환성

Android API 동작, `minSdk`/`targetSdk`, iOS 최소 버전, 의존성 지원 범위를 변경하는 작업은 [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md)(작성 예정)를 먼저 읽고 따른다.

## 성능 측정

이미지 로딩, 프레임 끊김, 메모리 같은 성능 작업을 변경·검증할 때는 [docs/PERFORMANCE.md](docs/PERFORMANCE.md)(작성 예정)를 먼저 읽고 따른다.

## 프로젝트 규칙

- Kotlin Multiplatform 프로젝트이며 공통 UI와 로직은 `commonMain`에 우선 작성한다. 공통 빌드 설정은 `build-logic`의 컨벤션 플러그인으로 관리한다.
- Kotlin 공식 코드 스타일을 따른다.
- 새 의존성은 구현에 필요한 이유와 영향을 확인한 뒤 추가한다. 버전은 검증된 안정 버전을 유지하며, 최신 버전이라는 이유만으로 올리지 않는다.
- 비밀값, 키, 로컬 환경 값은 `local.properties` 등 추적하지 않는 파일에 둔다.

## Git 규칙

- 커밋과 푸시는 사용자가 명시적으로 요청할 때만 수행한다.
- 브랜치 이름은 `이슈타입/#이슈번호-영문-설명` 형식이다. 이슈 타입은 `feat`, `fix`, `chore`, `mod` 등이다. 예: `feat/#12-pledge-create`
- 커밋 메시지는 Conventional Commits 형식에 한글 제목을 쓴다. 접두어는 브랜치의 이슈 타입과 같다. 예: `feat: 공약 생성 화면 추가`
- 이슈와 PR 본문은 `.github`의 템플릿을 따른다.

## 작업 마무리

- 요청 범위의 코드, 테스트, 관련 문서를 함께 갱신한다.
- 새 규칙이나 결정이 생기면 해당 단일 기준 문서를 갱신한다.
