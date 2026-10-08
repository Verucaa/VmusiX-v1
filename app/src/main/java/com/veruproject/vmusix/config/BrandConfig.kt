package com.veruproject.vmusix.config

import androidx.compose.ui.graphics.Color

/**
 * BrandConfig - Single Source of Truth untuk seluruh Identitas & Branding Aplikasi Vmusix.
 *
 * Pengguna atau pengembang dapat dengan mudah mengubah nama aplikasi, credit developer,
 * tagline, serta skema warna utama dan aksen cukup dari file ini.
 */
object BrandConfig {
    const val APP_NAME = "Vmusix"
    const val DEVELOPER_NAME = "VeruProject"
    const val DEVELOPER_EMAIL = "verucaadev@gmail.com"
    const val TAGLINE = "Listen Without Limits"
    const val VERSION_NAME = "1.0.0"

    // Path aset ikon aplikasi - mengganti file assets/app_icon.png otomatis mengubah tampilan ikon
    const val APP_ICON_ASSET = "file:///android_asset/app_icon.png"

    // Identitas Warna UI - AMOLED Friendly & Modern
    val PRIMARY_COLOR = Color(0xFF6D28D9)   // Deep Purple / Violet
    val SECONDARY_COLOR = Color(0xFF2563EB) // Royal Blue
    val BACKGROUND_COLOR = Color(0xFF09090B)// Deep AMOLED Black
    val CARD_COLOR = Color(0xFF18181B)      // Dark Surface Zinc
    val TEXT_COLOR = Color(0xFFFFFFFF)      // Pure White
    val ACCENT_COLOR = Color(0xFFA855F7)    // Neon Purple Accent
    val MUTED_TEXT = Color(0xFFA1A1AA)      // Zinc 400
    val BORDER_COLOR = Color(0xFF27272A)    // Zinc 800
    val SUCCESS_COLOR = Color(0xFF10B981)   // Emerald 500
    val ERROR_COLOR = Color(0xFFEF4444)     // Red 500
}
