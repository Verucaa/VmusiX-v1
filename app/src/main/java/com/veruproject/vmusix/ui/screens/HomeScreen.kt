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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.veruproject.vmusix.data.remote.HomeData
import com.veruproject.vmusix.domain.model.Album
import com.veruproject.vmusix.domain.model.Artist
import com.veruproject.vmusix.domain.model.Track
import com.veruproject.vmusix.ui.components.VmusixLoader

@Composable
fun HomeScreen(
    homeData: HomeData?,
    isLoading: Boolean,
    onTrackClick: (Track, List<Track>) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading || homeData == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VmusixLoader()
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Header Brand
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = BrandConfig.APP_NAME,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = BrandConfig.TAGLINE,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BrandConfig.ACCENT_COLOR,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Brand Icon Plate (Loads from assets or default)
                Box(
                    modifier = Modifier
                        .size(42.dp)
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
                        contentDescription = "Vmusix App Icon",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 1. Banner Rekomendasi
        if (homeData.bannerTracks.isNotEmpty()) {
            item {
                val banner = homeData.bannerTracks.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onTrackClick(banner, homeData.bannerTracks) }
                        .testTag("recommendation_banner")
                ) {
                    AsyncImage(
                        model = banner.thumbnailUrl,
                        contentDescription = banner.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC09090B), Color(0xF209090B))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = BrandConfig.ACCENT_COLOR,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REKOMENDASI HARI INI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = BrandConfig.ACCENT_COLOR
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${banner.artist} • ${banner.album}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onTrackClick(banner, homeData.bannerTracks) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandConfig.PRIMARY_COLOR
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("banner_play_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Putar Sekarang", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 2. Quick Picks
        item {
            SectionHeader(title = "Quick Picks", subtitle = "Dipilih berdasarkan selera musikmu")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(homeData.quickPicks) { track ->
                    QuickPickCard(
                        track = track,
                        onClick = { onTrackClick(track, homeData.quickPicks) }
                    )
                }
            }
        }

        // 3. Recently Played
        item {
            SectionHeader(title = "Baru Saja Diputar", subtitle = "Lanjutkan mendengarkan lagu favoritmu")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(homeData.recentlyPlayed) { track ->
                    RecentlyPlayedCard(
                        track = track,
                        onClick = { onTrackClick(track, homeData.recentlyPlayed) }
                    )
                }
            }
        }

        // 4. Trending Songs
        item {
            SectionHeader(title = "Trending Sekarang", subtitle = "Lagu terpopuler minggu ini di YouTube Music")
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                homeData.trending.forEachIndexed { index, track ->
                    TrendingTrackRow(
                        rank = index + 1,
                        track = track,
                        onClick = { onTrackClick(track, homeData.trending) }
                    )
                }
            }
        }

        // 5. New Releases (Albums)
        item {
            SectionHeader(title = "Rilis Baru", subtitle = "Album dan EP terbaru")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(homeData.newReleases) { album ->
                    AlbumCard(album = album, onClick = { onAlbumClick(album) })
                }
            }
        }

        // 6. Favorite & Top Artists
        item {
            SectionHeader(title = "Artis Teratas", subtitle = "Musisi yang paling sering didengarkan")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(homeData.topArtists) { artist ->
                    ArtistCard(artist = artist, onClick = { onArtistClick(artist) })
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
        )
    }
}

@Composable
private fun QuickPickCard(track: Track, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = track.thumbnailUrl,
            contentDescription = track.title,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BrandConfig.CARD_COLOR),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            text = track.artist,
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecentlyPlayedCard(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(220.dp)
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
                .size(54.dp)
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
                text = track.artist,
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrendingTrackRow(rank: Int, track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandConfig.CARD_COLOR)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) BrandConfig.ACCENT_COLOR else BrandConfig.MUTED_TEXT
            ),
            modifier = Modifier.width(32.dp)
        )
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
                text = "${track.artist} • ${track.album}",
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = track.durationFormatted,
            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
        )
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = album.thumbnailUrl,
            contentDescription = album.title,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BrandConfig.CARD_COLOR),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${album.artist} • ${album.year}",
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ArtistCard(artist: Artist, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = artist.thumbnailUrl,
            contentDescription = artist.name,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(BrandConfig.CARD_COLOR),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
