# Proov Design Tokens

- Source: Figma `Proov 작업 공간`
- Color: `⭐️color token` section (`232:17662`), Figma Variables 기준
- Typography: `⭐️typography` section (`234:17663`), Figma text styles 기준
- 이 문서의 값은 `get_variable_defs` 결과가 원본이다. 프레임 안에 적힌 라벨 텍스트와 다를 경우 변수 값을 따른다.

---

## 1. Color

토큰 이름은 Figma 변수 경로 그대로 사용한다.

### Primary / Green

| Token | Hex |
|---|---|
| Primary/Green/50 | `#F0FDF2` |
| Primary/Green/100 | `#DCF9E0` |
| Primary/Green/200 | `#BDF4C5` |
| Primary/Green/300 | `#89ED98` |
| Primary/Green/400 | `#4FE661` |
| Primary/Green/500 | `#32C949` |
| Primary/Green/600 | `#24A33A` |
| Primary/Green/700 | `#1F812F` |
| Primary/Green/800 | `#1B6328` |
| Primary/Green/900 | `#174A22` |

### Primary / Yellow

| Token | Hex |
|---|---|
| Primary/Yellow/50 | `#FFFDF0` |
| Primary/Yellow/100 | `#FFF8C9` |
| Primary/Yellow/200 | `#FFF19A` |
| Primary/Yellow/300 | `#FFE97A` |
| Primary/Yellow/400 | `#FFD84D` |
| Primary/Yellow/500 | `#F2BA20` |
| Primary/Yellow/700 | `#9B6A12` |
| Primary/Yellow/800 | `#704D14` |

> Yellow/600은 정의되어 있지 않다 (500 → 700으로 건너뜀).

### Neutral

| Token | Hex |
|---|---|
| Neutral/0 | `#FFFFFF` |
| Neutral/50 | `#FAFAF8` |
| Neutral/100 | `#F2F2EF` |
| Neutral/200 | `#E5E5E1` |
| Neutral/300 | `#D0D0CB` |
| Neutral/400 | `#A6A6A0` |
| Neutral/500 | `#7A7A75` |
| Neutral/600 | `#5B5B57` |
| Neutral/700 | `#3F3F3C` |
| Neutral/800 | `#292927` |
| Neutral/900 | `#1B1C1B` |
| Neutral/950 | `#0D0D0C` |

### System

단계는 50 / 200 / 400 / 600 / 800 다섯 개만 정의되어 있다.

| Token | Success | Warning | Danger |
|---|---|---|---|
| 50 | `#F0FDF2` | `#FFF6E8` | `#FFF1F0` |
| 200 | `#BDF4C5` | `#FFD6A3` | `#FFC7C3` |
| 400 | `#4FE661` | `#FF9F43` | `#FF6B61` |
| 600 | `#24A33A` | `#D96800` | `#D92D20` |
| 800 | `#1B6328` | `#7A3A00` | `#8A1C14` |

### 확인 필요한 불일치

**System/Success**: 변수 값은 Primary/Green의 50/200/400/600/800과 완전히 동일하다. 그런데 Figma 프레임 안 라벨에는 다른 hex가 적혀 있다.

| Step | 변수 값 (이 문서 기준) | 프레임 라벨 |
|---|---|---|
| 50 | `#F0FDF2` | `#ECFFF1` |
| 200 | `#BDF4C5` | `#A7FFBA` |
| 400 | `#4FE661` | `#3EFA6A` |
| 600 | `#24A33A` | `#16B23D` |
| 800 | `#1B6328` | `#126B2D` |

변수가 Green을 alias로 참조하는 건지, 라벨이 옛 값인지 이 데이터만으로는 알 수 없다. 디자이너에게 확인이 필요하다.

### 강조 표시 (추정)

- Green/400, Yellow/400 카드에 별(star) 아이콘이 붙어 있다.
- 메인 컬러를 뜻하는 표시로 보이지만 Figma에 명시된 설명은 없다. 디자이너 확인 전까지 추정으로 취급한다.

---

## 2. Typography

- Font family: **Pretendard** (전 스타일 공통)
- Letter spacing: 전 스타일 **-2%** (em 기준 -0.02em)
- 단위는 Figma px 기준. Android에서는 sp로 옮기면 된다.

| Style | Size | Weight | Line height | Letter spacing |
|---|---|---|---|---|
| Display/40 | 40 | Bold 700 | 52 | -2% |
| Heading/32 | 32 | Bold 700 | 42 | -2% |
| Heading/28 | 28 | Bold 700 | 38 | -2% |
| Heading/24 | 24 | Bold 700 | 34 | -2% |
| Title/22 | 22 | SemiBold 600 | 30 | -2% |
| Title/20 | 20 | SemiBold 600 | 28 | -2% |
| Title/18 | 18 | SemiBold 600 | 26 | -2% |
| Body/18 | 18 | Regular 400 | 28 | -2% |
| Body/16 | 16 | Regular 400 | 24 | -2% |
| Body/14 | 14 | Regular 400 | 22 | -2% |
| Label/16 | 16 | SemiBold 600 | 22 | -2% |
| Label/14 | 14 | SemiBold 600 | 20 | -2% |
| Caption/12 | 12 | Medium 500 | 18 | -2% |

참고: 디자인 파일 안에 "프루부체 만들어보기 (pretendard 너비 줄인 버전)"이라는 메모가 있다. 지금은 Pretendard 기준이고, 커스텀 폰트는 추후 계획으로 보인다.

---

## 3. 이 문서에 없는 것

- Spacing, radius, elevation 토큰: 확인한 노드(color/typography 섹션)에 없었다.
- Layout foundations 섹션(`236:21123`)에 iOS(393×852), Android(360×800) 템플릿과 Safe Area 구조가 있다. 수치 규칙만 확인했고 별도 토큰화는 하지 않았다.
- 컴포넌트(Tab Bar, App bar 등)의 토큰 바인딩은 읽지 않았다.
