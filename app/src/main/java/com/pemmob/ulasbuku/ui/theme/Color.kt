package com.pemmob.ulasbuku.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// PALET WARNA RESMI "ULASBUKU" — EDITORIAL COZY & PASTEL
// Buttermilk (#FFF1B5) + Pastel Blue (#C1DBE8) + Old Burgundy (#43302E)
// Kesan: Santai, ramah, berkarakter, namun tetap formal & elegan (bebas kesan AI kaku)
// =========================================================================

// 1. PALET WARNA INTI (Sesuai Referensi Pengguna)
val Buttermilk = Color(0xFFFFF1B5)          // Warm Creamy Buttermilk Yellow
val PastelBlue = Color(0xFFC1DBE8)          // Serene Soft Pastel Sky Blue
val OldBurgundy = Color(0xFF43302E)         // Deep Warm Burgundy Espresso (Teks, Tombol, Outline)

// Background & Surface
val PureWhite = Color(0xFFFFFFFF)           // Permukaan kartu putih bersih
val WarmCreamBg = Color(0xFFFAF7F2)         // Background layar hangat (nyaman untuk membaca)
val BackgroundWhite = WarmCreamBg

// Color Aliases untuk kompatibilitas layar lain
val ButtermilkLight = Color(0xFFFFF9E5)     // Aksen krem sangat lembut
val ButtermilkDark = Color(0xFFEFE0A2)
val ButtermilkGold = Color(0xFFE29D1C)
val PastelBlueDark = Color(0xFFA5C6D7)
val PastelBlueLight = Color(0xFFE4F0F6)
val PastelBlueAccent = Color(0xFF8BB7CD)

// 2. TEXT & BORDER (Old Burgundy based — Elegan & Ramah di Mata)
val TextPrimary = OldBurgundy               // Old Burgundy pekat — Teks Utama Tegas & Hangat
val TextSecondary = Color(0xFF6B5856)       // Muted Burgundy Slate — Subtitle & Penulis
val TextMuted = Color(0xFF9E8E8C)           // Warm Muted Gray — Placeholder
val BorderDark = OldBurgundy                // Garis outline tegas bernuansa Burgundy
val BorderSubtle = Color(0xFFE8DFD5)        // Garis pemisah hangat & halus
val SoftGray = Color(0xFFF7F4EF)            // Field input krem netral

// 3. CUTE PASTEL ACCENTS
val PastelBlueGradientStart = Color(0xFFC1DBE8)
val PastelBlueGradientEnd = Color(0xFFA5C6D7)

val PastelPeachGradientStart = Color(0xFFFFF1B5)
val PastelPeachGradientEnd = Color(0xFFFDE68A)

val PastelPurpleGradientStart = Color(0xFFE9D8FD)
val PastelPurpleGradientEnd = Color(0xFFD6BCFA)

val PastelGreenGradientStart = Color(0xFFD1FAE5)
val PastelGreenGradientEnd = Color(0xFFA7F3D0)

val PastelYellowGradientStart = Color(0xFFFFF1B5)
val PastelYellowGradientEnd = Color(0xFFFDE68A)

val PastelPinkGradientStart = Color(0xFFFED7E2)
val PastelPinkGradientEnd = Color(0xFFFBB6CE)

// Primary accent & aliases
val VividBlue = Color(0xFF3B6E8C)           // Rich Slate Blue accent
val OldBurgundyLight = Color(0xFF6A4E4B)
val OldBurgundyDark = Color(0xFF2C1E1D)
val DarkButton = OldBurgundy                // Tombol utama menggunakan Old Burgundy
val AmberStar = Color(0xFFF59E0B)           // Rating Star Warm Amber
val GreenSuccess = Color(0xFF10B981)
val CoralRed = Color(0xFFE11D48)

// Cover color generator by category ID with cute pastel tones
// Fungsi buat ngasih warna latar cover buku berdasarkan ID kategorinya
// Tiap kategori punya warna pastel yang beda-beda supaya gampang dibedain secara visual
fun getCategoryColor(categoryId: Int): Color = when (categoryId) {
    1 -> Color(0xFF93C5FD) // Cute Sky Blue (Fantasi)
    2 -> Color(0xFFC4B5FD) // Cute Lavender (Misteri)
    3 -> Color(0xFF6EE7B7) // Cute Mint Green (Inspiratif)
    4 -> Color(0xFFFDBA74) // Cute Warm Peach (Sastra)
    5 -> Color(0xFFFCD34D) // Cute Pastel Yellow (Non-Fiksi)
    else -> Color(0xFFCBD5E1)
}

// Fungsi buat ngasih warna background avatar penulis secara bergantian (cycling)
// Pakai modulo 4 supaya warnanya berputar dan nggak monoton
fun getAuthorAvatarBg(index: Int): Color = when (index % 4) {
    0 -> Color(0xFFFDE68A) // Yellow
    1 -> Color(0xFFFBCFE8) // Pink
    2 -> Color(0xFFBFDBFE) // Sky Blue
    else -> Color(0xFFA7F3D0) // Mint
}