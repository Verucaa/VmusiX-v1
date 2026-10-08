package com.veruproject.vmusix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.LyricLine
import com.veruproject.vmusix.domain.model.LyricsData

@Composable
fun SyncedLyricsView(
    lyricsData: LyricsData?,
    currentPositionMs: Long,
    isLoading: Boolean,
    onLineClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VmusixLoader()
        }
        return
    }

    if (lyricsData == null || lyricsData.lines.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Lirik Belum Tersedia",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandConfig.MUTED_TEXT
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lirik untuk lagu ini belum ditemukan pada provider",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val lines = lyricsData.lines
    val listState = rememberLazyListState()

    // Cari index baris aktif berdasarkan audio position
    val activeIndex = lines.indexOfLast { it.timeMs <= currentPositionMs }.coerceAtLeast(0)

    // Auto-Scroll smooth mengikuti posisi audio
    LaunchedEffect(activeIndex) {
        if (activeIndex in lines.indices) {
            listState.animateScrollToItem(
                index = activeIndex,
                scrollOffset = -220 // Centering effect
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Provider info tag
        Text(
            text = "Sumber Lirik: ${lyricsData.provider}",
            style = MaterialTheme.typography.labelSmall,
            color = BrandConfig.ACCENT_COLOR.copy(alpha = 0.8f),
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("lyrics_list"),
            contentPadding = PaddingValues(vertical = 120.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            itemsIndexed(lines) { index, line ->
                val isActive = index == activeIndex
                val isPast = index < activeIndex

                val textColor by animateColorAsState(
                    targetValue = when {
                        isActive -> Color.White
                        isPast -> Color.White.copy(alpha = 0.5f)
                        else -> BrandConfig.MUTED_TEXT.copy(alpha = 0.4f)
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "lyrics_text_color"
                )

                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.06f else 0.98f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "lyrics_scale"
                )

                Text(
                    text = line.text,
                    style = if (isActive) {
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textColor
                        )
                    } else {
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .clickable {
                            onLineClick(line.timeMs)
                        }
                        .padding(vertical = 4.dp)
                        .testTag("lyric_line_$index")
                )
            }
        }
    }
}
