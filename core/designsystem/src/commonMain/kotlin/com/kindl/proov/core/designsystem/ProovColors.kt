package com.kindl.proov.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ProovColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val ink: Color,
    val muted: Color,
    val line: Color,
    val brand: Color,
    val brand2: Color,
    val brandSoft: Color,
    val green: Color,
    val greenSoft: Color,
    val orange: Color,
    val orangeSoft: Color,
    val red: Color,
    val redSoft: Color,
    val scrim: Color,
    val onBrand: Color,
)

internal val ProovLightColors = ProovColors(
    bg = Color(0xFFF2F6F5),
    surface = Color(0xFFFFFFFF),
    surface2 = Color(0xFFF7FAF9),
    ink = Color(0xFF102A32),
    muted = Color(0xFF6D7D80),
    line = Color(0xFFDFE9E6),
    brand = Color(0xFF173B4F),
    brand2 = Color(0xFF255D70),
    brandSoft = Color(0xFFE9F0F2),
    green = Color(0xFF2EAE78),
    greenSoft = Color(0xFFE7F7EF),
    orange = Color(0xFFD89127),
    orangeSoft = Color(0xFFFFF4E4),
    red = Color(0xFFD76666),
    redSoft = Color(0xFFFCECEC),
    scrim = Color(0x6B071319), // rgba(7,19,25,.42)
    onBrand = Color(0xFFFFFFFF),
)
