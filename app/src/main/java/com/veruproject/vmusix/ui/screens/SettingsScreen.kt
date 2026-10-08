package com.veruproject.vmusix.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veruproject.vmusix.config.BrandConfig

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    audioQuality: String,
    downloadQuality: String,
    lyricsSource: String,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAudioQuality: (String) -> Unit,
    onSelectDownloadQuality: (String) -> Unit,
    onSelectLyricsSource: (String) -> Unit,
    onClearCache: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showAudioMenu by remember { mutableStateOf(false) }
    var showDownloadMenu by remember { mutableStateOf(false) }
    var showLyricsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandConfig.BACKGROUND_COLOR)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pengaturan",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance & Themes
            item {
                SettingsSectionTitle("Tampilan & Tema")
            }

            item {
                SettingsCard {
                    SettingsToggleItem(
                        icon = Icons.Filled.DarkMode,
                        title = "Mode Gelap (AMOLED)",
                        subtitle = "Warna hitam murni ramah layar AMOLED",
                        checked = darkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }

            // Audio & Playback
            item {
                SettingsSectionTitle("Kualitas Suara & Unduhan")
            }

            item {
                SettingsCard {
                    // Audio Quality Selector
                    Box {
                        SettingsClickableItem(
                            icon = Icons.Filled.Audiotrack,
                            title = "Kualitas Streaming Audio",
                            subtitle = audioQuality,
                            onClick = { showAudioMenu = true }
                        )
                        DropdownMenu(
                            expanded = showAudioMenu,
                            onDismissRequest = { showAudioMenu = false },
                            modifier = Modifier.background(BrandConfig.CARD_COLOR)
                        ) {
                            listOf("Hemat Data (96kbps)", "Standar (160kbps)", "Tinggi (320kbps)").forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q, color = Color.White) },
                                    onClick = {
                                        onSelectAudioQuality(q)
                                        showAudioMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Download Quality Selector
                    Box {
                        SettingsClickableItem(
                            icon = Icons.Filled.Download,
                            title = "Kualitas Unduhan Offline",
                            subtitle = downloadQuality,
                            onClick = { showDownloadMenu = true }
                        )
                        DropdownMenu(
                            expanded = showDownloadMenu,
                            onDismissRequest = { showDownloadMenu = false },
                            modifier = Modifier.background(BrandConfig.CARD_COLOR)
                        ) {
                            listOf("Standar (160kbps)", "Tinggi (320kbps)", "FLAC / Lossless").forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q, color = Color.White) },
                                    onClick = {
                                        onSelectDownloadQuality(q)
                                        showDownloadMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Lyrics Source Priority
            item {
                SettingsSectionTitle("Lirik Musik")
            }

            item {
                SettingsCard {
                    Box {
                        SettingsClickableItem(
                            icon = Icons.Filled.FormatQuote,
                            title = "Sumber Prioritas Lirik",
                            subtitle = lyricsSource,
                            onClick = { showLyricsMenu = true }
                        )
                        DropdownMenu(
                            expanded = showLyricsMenu,
                            onDismissRequest = { showLyricsMenu = false },
                            modifier = Modifier.background(BrandConfig.CARD_COLOR)
                        ) {
                            listOf(
                                "Auto (Cascade: YTM -> LRCLIB -> BetterLyrics -> KuGou -> Paxsenix)",
                                "LRCLIB (Synced)",
                                "Better Lyrics",
                                "KuGou Lyrics",
                                "Paxsenix Lyrics"
                            ).forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = Color.White) },
                                    onClick = {
                                        onSelectLyricsSource(s)
                                        showLyricsMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Storage & Cache
            item {
                SettingsSectionTitle("Penyimpanan")
            }

            item {
                SettingsCard {
                    SettingsClickableItem(
                        icon = Icons.Filled.Storage,
                        title = "Bersihkan Cache Aplikasi",
                        subtitle = "Menghapus cache gambar dan stream sementara",
                        onClick = onClearCache
                    )
                }
            }

            // About Brand & Developer Credit
            item {
                SettingsSectionTitle("Tentang Aplikasi")
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BrandConfig.PRIMARY_COLOR, BrandConfig.ACCENT_COLOR)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = BrandConfig.APP_ICON_ASSET,
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = BrandConfig.APP_NAME,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = BrandConfig.TAGLINE,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BrandConfig.ACCENT_COLOR,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Versi ${BrandConfig.VERSION_NAME} • Dikembangkan oleh ${BrandConfig.DEVELOPER_NAME}",
                            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Email: ${BrandConfig.DEVELOPER_EMAIL}",
                            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF27272A))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "💡 Seluruh branding dapat diganti hanya melalui file BrandConfig.kt dan mengganti assets/app_icon.png tanpa mengubah logika program.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = BrandConfig.ACCENT_COLOR
        )
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(BrandConfig.PRIMARY_COLOR.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandConfig.ACCENT_COLOR, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandConfig.PRIMARY_COLOR
            )
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(BrandConfig.PRIMARY_COLOR.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandConfig.ACCENT_COLOR, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT))
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = BrandConfig.MUTED_TEXT
        )
    }
}
