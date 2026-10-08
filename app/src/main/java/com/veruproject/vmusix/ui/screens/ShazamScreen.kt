package com.veruproject.vmusix.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.ShazamResult
import com.veruproject.vmusix.domain.model.Track

@Composable
fun ShazamScreen(
    isListening: Boolean,
    recognizedResult: ShazamResult?,
    onStartListening: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val transition = rememberInfiniteTransition(label = "shazam_wave")
    val waveScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandConfig.BACKGROUND_COLOR)
            .padding(20.dp)
            .testTag("shazam_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
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
                text = "Shazam Pengenal Musik",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isListening) "Mendengarkan Musik di Sekitar..." else "Ketuk untuk Mengenali Lagu",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isListening) BrandConfig.ACCENT_COLOR else Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Didukung teknologi audio fingerprinting ShazamKit",
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Large Listening Mic Button with Wave Animation
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isListening) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = (size.minDimension / 2) * (waveScale - 0.2f)
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = (2f - waveScale).coerceIn(0f, 0.4f)),
                        radius = radius,
                        style = Stroke(width = 8f)
                    )
                    drawCircle(
                        color = BrandConfig.ACCENT_COLOR.copy(alpha = (2f - waveScale).coerceIn(0f, 0.3f)),
                        radius = radius * 0.7f,
                        style = Stroke(width = 6f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0284C7), BrandConfig.PRIMARY_COLOR, BrandConfig.ACCENT_COLOR)
                        )
                    )
                    .clickable(enabled = !isListening) { onStartListening() }
                    .testTag("shazam_listen_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Mulai identifikasi",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Recognized Result Card
        if (recognizedResult != null) {
            val track = Track(
                id = "shazam_${recognizedResult.title}",
                title = recognizedResult.title,
                artist = recognizedResult.artist,
                album = recognizedResult.album,
                thumbnailUrl = recognizedResult.coverUrl,
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandConfig.CARD_COLOR),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shazam_result_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = recognizedResult.coverUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recognizedResult.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = recognizedResult.artist,
                                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
                            )
                            Text(
                                text = "Kecocokan: ${(recognizedResult.matchConfidence * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.SUCCESS_COLOR)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlayTrack(track) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandConfig.PRIMARY_COLOR),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka Lagu")
                        }

                        OutlinedButton(
                            onClick = { onAddToPlaylist(track) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.PlaylistAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
