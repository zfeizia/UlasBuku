package com.pemmob.ulasbuku.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// PALET WARNA "ULASBUKU" — CUTIE PASTEL POP & PURE WHITE
// Terinspirasi referensi UI "Hello, Jenny" modern cute retro pop
// Konsep: Pure White Screen + Cute Pastel Gradients + Soft Dark Borders
// =========================================================================

// 1. MAIN BACKGROUND & SURFACE
val PureWhite = Color(0xFFFFFFFF)          // Background layar utama & kartu
val BackgroundWhite = PureWhite
val Buttermilk = PureWhite
val PastelBlue = PureWhite

// Color Aliases for backward compatibility with detail & review screens
val ButtermilkLight = Color(0xFFF8FAFC)
val ButtermilkDark = Color(0xFFE2E8F0)
val ButtermilkGold = Color(0xFFFFB800)
val PastelBlueDark = Color(0xFFE2E8F0)
val PastelBlueLight = Color(0xFFFAFBFC)
val PastelBlueAccent = Color(0xFFCBD5E1)

// 2. TEXT & BORDER (Neo-brutalist / Soft Pop Retro styling)
val TextPrimary = Color(0xFF0F172A)        // Charcoal Black — Teks Utama Bold
val TextSecondary = Color(0xFF475569)      // Slate Gray — Subtitle & Penulis
val TextMuted = Color(0xFF94A3B8)          // Muted Gray — Placeholder
val BorderDark = Color(0xFF1E293B)         // Border Hitam Halus untuk elemen cute
val BorderSubtle = Color(0xFFE2E8F0)       // Garis pemisah halus
val SoftGray = Color(0xFFF8FAFC)           // Field input & background item netral

// 3. CUTE PASTEL GRADIENTS (Banner, Avatar, Chips, & Accent Cards)
val PastelBlueGradientStart = Color(0xFFBFDBFE)  // Cute Sky Blue
val PastelBlueGradientEnd = Color(0xFF93C5FD)

val PastelPeachGradientStart = Color(0xFFFED7AA) // Cute Peach
val PastelPeachGradientEnd = Color(0xFFFDBA74)

val PastelPurpleGradientStart = Color(0xFFDDD6FE) // Cute Lavender
val PastelPurpleGradientEnd = Color(0xFFC4B5FD)

val PastelGreenGradientStart = Color(0xFFA7F3D0)  // Cute Mint
val PastelGreenGradientEnd = Color(0xFF6EE7B7)

val PastelYellowGradientStart = Color(0xFFFDE68A) // Cute Cream Yellow
val PastelYellowGradientEnd = Color(0xFFFCD34D)

val PastelPinkGradientStart = Color(0xFFFBCFE8)   // Cute Rose Pink
val PastelPinkGradientEnd = Color(0xFFF472B6)

// Primary accent & aliases
val VividBlue = Color(0xFF2563EB)          // Vibrant Royal Blue accent
val OldBurgundy = VividBlue
val OldBurgundyLight = Color(0xFF60A5FA)
val OldBurgundyDark = TextPrimary
val DarkButton = Color(0xFF0F172A)         // Dark Charcoal Pill Button
val AmberStar = Color(0xFFFFB800)          // Rating Star Amber
val GreenSuccess = Color(0xFF10B981)
val CoralRed = Color(0xFFEF4444)

// Cover color generator by category ID with cute pastel tones
fun getCategoryColor(categoryId: Int): Color = when (categoryId) {
    1 -> Color(0xFF93C5FD) // Cute Sky Blue (Fantasi)
    2 -> Color(0xFFC4B5FD) // Cute Lavender (Misteri)
    3 -> Color(0xFF6EE7B7) // Cute Mint Green (Inspiratif)
    4 -> Color(0xFFFDBA74) // Cute Warm Peach (Sastra)
    5 -> Color(0xFFFCD34D) // Cute Pastel Yellow (Non-Fiksi)
    else -> Color(0xFFCBD5E1)
}

fun getAuthorAvatarBg(index: Int): Color = when (index % 4) {
    0 -> Color(0xFFFDE68A) // Yellow
    1 -> Color(0xFFFBCFE8) // Pink
    2 -> Color(0xFFBFDBFE) // Sky Blue
    else -> Color(0xFFA7F3D0) // Mint
}