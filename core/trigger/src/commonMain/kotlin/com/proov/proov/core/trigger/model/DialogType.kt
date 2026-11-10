package com.proov.proov.core.trigger.model

/**
 * 앱 전역에서 공통으로 쓰는 다이얼로그의 문구와 표시 속성이다.
 * 확인을 눌렀을 때의 동작은 호출하는 쪽마다 다르므로 [DialogTrigger.show]의 파라미터로 넘긴다.
 */
// TODO: 여기에는 앱 공통 정책(네트워크 오류, 찾을 수 없음 등)만 추가한다.
//  한 기능에서만 쓰는 다이얼로그는 해당 기능 화면에서 ProovDialog를 직접 선언한다.
enum class DialogType(
    val title: String,
    val confirmText: String,
    val content: String? = null,
    val dismissible: Boolean = true,
) {
    NETWORK(
        title = "인터넷 연결이 끊겼어요",
        confirmText = "재시도",
        dismissible = false,
    ),
}
