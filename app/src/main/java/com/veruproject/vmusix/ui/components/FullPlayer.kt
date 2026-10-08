package com.veruproject.vmusix.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.LyricsData
import com.veruproject.vmusix.domain.model.Track

@Composable
fun FullPlayer(
    visible: Boolean,
    track: Track?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    isShuffle: Boolean,
    isRepeat: Boolean,
    queue: List<Track>,
    lyricsData: LyricsData?,
    isLyricsLoading: Boolean,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: (Track) -> Unit,
    onDownload: (Track) -> Unit,
    onQueueTrackClick: (Track) -> Unit
) {
    if (!visible || track == null) return

    BackHandler {
        onDismiss()
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Player, 1: Lyrics, 2: Queue
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragPositionMs by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isDraggingSlider) dragPositionMs.toLong() else currentPositionMs
    val progressFraction = if (durationMs > 0) (effectivePosition.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandConfig.BACKGROUND_COLOR)
                .statusBarsPadding()
                .testTag("full_player_screen")
        ) {
            // Background ambient glow / gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrandConfig.PRIMARY_COLOR.copy(alpha = 0.35f),
                                BrandConfig.BACKGROUND_COLOR.copy(alpha = 0.85f),
                                BrandConfig.BACKGROUND_COLOR
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_full_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Tutup player",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SEDANG MEMUTAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandConfig.ACCENT_COLOR
                            )
                        )
                        Text(
                            text = track.album.ifEmpty { "Vmusix Stream" },
                            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onToggleLike(track) },
                        modifier = Modifier.testTag("like_track_button")
                    ) {
                        Icon(
                            imageVector = if (track.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (track.isLiked) "Hapus dari disukai" else "Sukai lagu",
                            tint = if (track.isLiked) Color.Red else Color.White
                        )
                    }
                }

                // Tab Switcher (Player, Lirik, Antrean)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BrandConfig.ACCENT_COLOR
                        )
                    },
                    divider = {},
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Player", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Lirik", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Antrean", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content based on tab
                when (selectedTab) {
                    0 -> {
                        // Main Player View with Large Artwork
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Large Rounded Cover
                            AsyncImage(
                                model = track.thumbnailUrl,
                                contentDescription = "Cover ${track.title}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(BrandConfig.CARD_COLOR)
                                    .testTag("full_player_cover")
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // Track Info & Download action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = BrandConfig.MUTED_TEXT
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                IconButton(
                                    onClick = { onDownload(track) },
                                    modifier = Modifier.testTag("download_track_button")
                                ) {
                                    Icon(
                                        imageVector = if (track.isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download,
                                        contentDescription = "Unduh lagu",
                                        tint = if (track.isDownloaded) BrandConfig.SUCCESS_COLOR else Color.White
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Synced Lyrics View
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            SyncedLyricsView(
                                lyricsData = lyricsData,
                                currentPositionMs = currentPositionMs,
                                isLoading = isLyricsLoading,
                                onLineClick = { timeMs -> onSeek(timeMs) }
                            )
                        }
                    }

                    2 -> {
                        // Queue View
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("queue_list"),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(queue) { qTrack ->
                                val isCurrent = qTrack.id == track.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isCurrent) BrandConfig.CARD_COLOR else Color.Transparent)
                                        .clickable { onQueueTrackClick(qTrack) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = qTrack.thumbnailUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = qTrack.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = if (isCurrent) BrandConfig.ACCENT_COLOR else Color.White,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = qTrack.artist,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = BrandConfig.MUTED_TEXT
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Filled.MusicNote,
                                            contentDescription = null,
                                            tint = BrandConfig.ACCENT_COLOR,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Progress Slider Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Slider(
                        value = progressFraction,
                        onValueChange = { fraction ->
                            isDraggingSlider = true
                            dragPositionMs = fraction * durationMs
                        },
                        onValueChangeFinished = {
                            isDraggingSlider = false
                            onSeek(dragPositionMs.toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = BrandConfig.ACCENT_COLOR,
                            inactiveTrackColor = Color(0xFF27272A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_progress_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(effectivePosition),
                            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
                        )
                        Text(
                            text = formatTime(durationMs),
                            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Playback Control Buttons (Shuffle, Prev, Play/Pause, Next, Repeat)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier.testTag("shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Acak antrean",
                            tint = if (isShuffle) BrandConfig.ACCENT_COLOR else BrandConfig.MUTED_TEXT
                        )
                    }

                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Lagu sebelumnya",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(BrandConfig.PRIMARY_COLOR, BrandConfig.ACCENT_COLOR)
                                )
                            )
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Jeda musik" else "Putar musik",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Lagu selanjutnya",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier.testTag("repeat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Repeat,
                            contentDescription = "Ulangi lagu",
                            tint = if (isRepeat) BrandConfig.ACCENT_COLOR else BrandConfig.MUTED_TEXT
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
