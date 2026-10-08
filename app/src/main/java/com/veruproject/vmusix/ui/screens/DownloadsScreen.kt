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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.DownloadItem
import com.veruproject.vmusix.domain.model.DownloadStatus
import com.veruproject.vmusix.domain.model.Track

@Composable
fun DownloadsScreen(
    downloads: Map<String, DownloadItem>,
    downloadedTracks: List<Track>,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRetry: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val activeDownloads = downloads.values.filter {
        it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.QUEUED
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandConfig.BACKGROUND_COLOR)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("downloads_screen")
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
            Column {
                Text(
                    text = "Musik Diunduh",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Tersimpan di Music/Vmusix (Siap Offline)",
                    style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Download Queue Section
            if (activeDownloads.isNotEmpty()) {
                item {
                    Text(
                        text = "Sedang Mengunduh (${activeDownloads.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandConfig.ACCENT_COLOR
                        )
                    )
                }

                items(activeDownloads) { item ->
                    ActiveDownloadCard(
                        item = item,
                        onPause = { onPause(item.id) },
                        onResume = { onResume(item.id) },
                        onCancel = { onCancel(item.id) },
                        onRetry = { onRetry(item.id) }
                    )
                }
            }

            // Completed Offline Tracks
            item {
                Text(
                    text = "Lagu Tersimpan Offline (${downloadedTracks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            if (downloadedTracks.isEmpty() && activeDownloads.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada lagu yang diunduh.\nUnduh lagu favoritmu untuk diputar tanpa internet!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(downloadedTracks) { track ->
                    DownloadedTrackRow(
                        track = track,
                        onClick = { onPlayTrack(track, downloadedTracks) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveDownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BrandConfig.CARD_COLOR)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.track.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.track.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.track.artist} • ${(item.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.ACCENT_COLOR)
                )
            }

            // Controls (Pause, Resume, Cancel, Retry)
            when (item.status) {
                DownloadStatus.DOWNLOADING -> {
                    IconButton(onClick = onPause) {
                        Icon(Icons.Filled.Pause, contentDescription = "Jeda", tint = Color.White)
                    }
                }
                DownloadStatus.PAUSED -> {
                    IconButton(onClick = onResume) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Lanjutkan", tint = Color.White)
                    }
                }
                DownloadStatus.FAILED -> {
                    IconButton(onClick = onRetry) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Ulangi", tint = BrandConfig.ERROR_COLOR)
                    }
                }
                else -> {}
            }

            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = "Batal", tint = BrandConfig.MUTED_TEXT)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { item.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = BrandConfig.ACCENT_COLOR,
            trackColor = Color(0xFF27272A)
        )
    }
}

@Composable
private fun DownloadedTrackRow(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandConfig.CARD_COLOR)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.thumbnailUrl,
            contentDescription = track.title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • Offline Audio",
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.SUCCESS_COLOR),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
