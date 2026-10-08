package com.veruproject.vmusix.ui.screens

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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.data.cache.CacheStats

@Composable
fun MoreScreen(
    cacheSize: String,
    cacheStats: CacheStats?,
    likedCount: Int,
    downloadCount: Int,
    onNavigateLiked: () -> Unit,
    onNavigateDownloads: () -> Unit,
    onNavigateTop50: () -> Unit,
    onNavigatePlaylists: () -> Unit,
    onNavigateUploads: () -> Unit,
    onNavigateShazam: () -> Unit,
    onNavigateLastFm: () -> Unit,
    onNavigateSettings: () -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Koleksi & Lainnya",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kelola library, unduhan, pengenal musik, dan preferensi Vmusix",
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
            )
        }

        // 1. Modern Card: Cache Management
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cache_management_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BrandConfig.PRIMARY_COLOR.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Storage,
                                    contentDescription = null,
                                    tint = BrandConfig.ACCENT_COLOR,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Manajemen Cache",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Total Digunakan: $cacheSize",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = BrandConfig.ACCENT_COLOR,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onClearCache,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF27272A))
                                .testTag("clear_cache_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Hapus cache",
                                tint = BrandConfig.ERROR_COLOR
                            )
                        }
                    }

                    if (cacheStats != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CacheStatItem(label = "Gambar", value = cacheStats.imageFormatted)
                            CacheStatItem(label = "Stream", value = cacheStats.audioStreamFormatted)
                            CacheStatItem(label = "Lirik", value = cacheStats.lyricsFormatted)
                        }
                    }
                }
            }
        }

        // 2. Disukai (Favorites) & Diunduh (Downloads) Grid Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Disukai
                ModernActionCard(
                    title = "Disukai",
                    subtitle = "$likedCount Lagu",
                    icon = Icons.Filled.Favorite,
                    iconTint = Color(0xFFEF4444),
                    onClick = onNavigateLiked,
                    modifier = Modifier.weight(1f)
                )

                // Diunduh
                ModernActionCard(
                    title = "Diunduh",
                    subtitle = "$downloadCount Offline",
                    icon = Icons.Filled.DownloadDone,
                    iconTint = BrandConfig.SUCCESS_COLOR,
                    onClick = onNavigateDownloads,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. 50 Teratas & Diunggah
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 50 Teratas
                ModernActionCard(
                    title = "50 Teratas",
                    subtitle = "Tangga Lagu Global",
                    icon = Icons.Filled.Leaderboard,
                    iconTint = BrandConfig.ACCENT_COLOR,
                    onClick = onNavigateTop50,
                    modifier = Modifier.weight(1f)
                )

                // Diunggah
                ModernActionCard(
                    title = "Diunggah",
                    subtitle = "Lagu Lokal Perangkat",
                    icon = Icons.Filled.CloudUpload,
                    iconTint = BrandConfig.SECONDARY_COLOR,
                    onClick = onNavigateUploads,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Playlist Card
        item {
            ModernFullWidthCard(
                title = "Playlist Saya",
                subtitle = "Kelola dan susun daftar putar musik favorit Anda",
                icon = Icons.Filled.QueueMusic,
                iconTint = Color(0xFFF59E0B),
                onClick = onNavigatePlaylists
            )
        }

        // 5. Shazam Music Recognition Card
        item {
            ModernFullWidthCard(
                title = "Shazam Pengenal Musik",
                subtitle = "Dengarkan dan temukan judul lagu yang sedang berputar di sekitar",
                icon = Icons.Filled.Mic,
                iconTint = Color(0xFF38BDF8),
                badge = "ShazamKit",
                onClick = onNavigateShazam
            )
        }

        // 6. Last.fm Scrobbler Card
        item {
            ModernFullWidthCard(
                title = "Last.fm Scrobbling",
                subtitle = "Sinkronkan riwayat pemutaran dan statistik musik real-time",
                icon = Icons.Filled.Radio,
                iconTint = Color(0xFFD92626),
                badge = "Last.fm API",
                onClick = onNavigateLastFm
            )
        }

        // 7. Pengaturan Aplikasi
        item {
            ModernFullWidthCard(
                title = "Pengaturan",
                subtitle = "Kualitas audio, tema, sumber lirik, dan info developer VeruProject",
                icon = Icons.Filled.Settings,
                iconTint = Color.White,
                onClick = onNavigateSettings
            )
        }
    }
}

@Composable
private fun CacheStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun ModernActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
            )
        }
    }
}

@Composable
private fun ModernFullWidthCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(iconTint.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = iconTint,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
                )
            }
        }
    }
}
