package com.proov.proov.core.common.extension

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

// 탭하거나 IME의 next로 포커스가 넘어올 때, 가상 키보드 위로 스크롤해서 보여준다
fun Modifier.focusScrollable(viewportHeight: Int = 0): Modifier = composed {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused, viewportHeight) {
        if (isFocused) {
            withFrameNanos {}
            bringIntoViewRequester.bringIntoView()
        }
    }

    this
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent { focusState ->
            isFocused = focusState.isFocused
        }
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = this then NoRippleClickableElement(
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = onClick,
)

private data class NoRippleClickableElement(
    val enabled: Boolean,
    val onClickLabel: String?,
    val role: Role?,
    val onClick: () -> Unit,
) : ModifierNodeElement<NoRippleClickableNode>() {

    override fun create(): NoRippleClickableNode = NoRippleClickableNode(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )

    override fun update(node: NoRippleClickableNode) {
        node.enabled = enabled
        node.onClickLabel = onClickLabel
        node.role = role
        node.onClick = onClick
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "noRippleClickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
    }
}

private class NoRippleClickableNode(
    var enabled: Boolean,
    var onClickLabel: String?,
    var role: Role?,
    var onClick: () -> Unit,
) : Modifier.Node(),
    PointerInputModifierNode,
    SemanticsModifierNode {

    private var pointerDown = false

    override fun onPointerEvent(pointerEvent: PointerEvent, pass: PointerEventPass, bounds: IntSize) {
        if (!enabled || pass != PointerEventPass.Main) return

        when (pointerEvent.type) {
            PointerEventType.Press -> {
                if (pointerEvent.changes.any { it.pressed }) {
                    pointerDown = true
                    pointerEvent.changes.forEach { it.consume() }
                }
            }

            PointerEventType.Release -> {
                val releasedInside = pointerEvent.changes.any { change ->
                    change.changedToUp() &&
                        !change.isOutOfBounds(
                            size = bounds,
                            extendedTouchPadding = Size.Zero,
                        )
                }

                val shouldClick = pointerDown && releasedInside
                pointerDown = false

                if (shouldClick) {
                    pointerEvent.changes.forEach { it.consume() }
                    onClick()
                }
            }

            PointerEventType.Exit -> {
                pointerDown = false
            }
        }
    }

    override fun onCancelPointerInput() {
        pointerDown = false
    }

    override fun SemanticsPropertyReceiver.applySemantics() {
        if (enabled) {
            role?.let { role = it }

            onClick(
                label = onClickLabel,
            ) {
                onClick()
                true
            }
        }
    }
}

// LazyColumn 같이 스크롤 후 더 이상 스크롤할 곳이 없을 때 바텀시트 움직임 막기
fun Modifier.disableNestedScroll(): Modifier = composed {
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
                available

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = available
        }
    }
    this.nestedScroll(connection)
}

/**
 * 바텀시트 내부에서 위로 당기는(Upward) 드래그, 스크롤 이벤트만 가로채서 소비하는 Modifier
 *
 * touch slop을 넘기 전에는 소비하지 않는다. slop 판정 전에 소비해버리면 자식 스크롤(휠 피커 등)이
 * 제스처를 빼앗긴 것으로 판단하고 드래그 판정을 취소하기 때문. 자식이 먼저 소비한 이벤트도 건드리지 않는다.
 */
fun Modifier.disableUpWardEvent(): Modifier = this.pointerInput(Unit) {
    val touchSlop = viewConfiguration.touchSlop

    awaitPointerEventScope {
        var accumulatedDy = 0f
        var isPastSlop = false

        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)

            event.changes.forEach { change ->
                if (change.changedToDownIgnoreConsumed()) {
                    accumulatedDy = 0f
                    isPastSlop = false
                }

                if (change.isConsumed || !change.pressed) return@forEach

                val dy = change.position.y - change.previousPosition.y

                if (!isPastSlop) {
                    accumulatedDy += dy
                    isPastSlop = abs(accumulatedDy) > touchSlop
                }

                // 손가락이 위로 향할 때만 차단
                if (isPastSlop && dy < 0) {
                    change.consume()
                }
            }
        }
    }
}
