package com.veruproject.vmusix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.Track
import com.veruproject.vmusix.ui.components.FullPlayer
import com.veruproject.vmusix.ui.components.MiniPlayer
import com.veruproject.vmusix.ui.screens.DownloadsScreen
import com.veruproject.vmusix.ui.screens.HomeScreen
import com.veruproject.vmusix.ui.screens.LastFmScreen
import com.veruproject.vmusix.ui.screens.LikedTracksScreen
import com.veruproject.vmusix.ui.screens.MoreScreen
import com.veruproject.vmusix.ui.screens.SearchScreen
import com.veruproject.vmusix.ui.screens.SettingsScreen
import com.veruproject.vmusix.ui.screens.ShazamScreen
import com.veruproject.vmusix.ui.theme.VmusixTheme
import com.veruproject.vmusix.ui.viewmodel.MainViewModel
import com.veruproject.vmusix.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkMode by viewModel.darkMode.collectAsState()

            VmusixTheme(darkTheme = darkMode) {
                VmusixApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VmusixApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isFullPlayerVisible by viewModel.isFullPlayerVisible.collectAsState()

    // Playback state
    val currentTrack by viewModel.musicPlayer.currentTrack.collectAsState()
    val isPlaying by viewModel.musicPlayer.isPlaying.collectAsState()
    val currentPositionMs by viewModel.musicPlayer.currentPosition.collectAsState()
    val durationMs by viewModel.musicPlayer.duration.collectAsState()
    val queue by viewModel.musicPlayer.queue.collectAsState()
    val isShuffle by viewModel.musicPlayer.isShuffle.collectAsState()
    val isRepeat by viewModel.musicPlayer.isRepeat.collectAsState()

    // Lyrics state
    val currentLyrics by viewModel.currentLyrics.collectAsState()
    val isLyricsLoading by viewModel.isLyricsLoading.collectAsState()

    // Progress fraction
    val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    // Liked & Downloaded states
    val likedTracks by viewModel.likedTracks.collectAsState()
    val downloadedTracks by viewModel.downloadedTracks.collectAsState()
    val downloads by viewModel.downloads.collectAsState()

    // Handle back button when on sub-screens
    if (currentScreen != Screen.HOME) {
        BackHandler {
            when (currentScreen) {
                Screen.SEARCH -> viewModel.navigateTo(Screen.HOME)
                Screen.MORE -> viewModel.navigateTo(Screen.HOME)
                Screen.SHAZAM,
                Screen.DOWNLOADS,
                Screen.LIKED,
                Screen.LASTFM,
                Screen.SETTINGS -> viewModel.navigateTo(Screen.MORE)
                else -> viewModel.navigateTo(Screen.HOME)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandConfig.BACKGROUND_COLOR)
    ) {
        Scaffold(
            containerColor = BrandConfig.BACKGROUND_COLOR,
            bottomBar = {
                // Show bottom navigation bar only for top-level screens
                if (currentScreen == Screen.HOME || currentScreen == Screen.SEARCH || currentScreen == Screen.MORE) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Sticky MiniPlayer above Bottom Navigation
                        MiniPlayer(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            progress = progress,
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onNextClick = { viewModel.playNext() },
                            onClick = { viewModel.showFullPlayer() }
                        )

                        // 3-Menu Bottom Navigation (Home, Cari, Lainnya)
                        NavigationBar(
                            containerColor = BrandConfig.CARD_COLOR,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .testTag("main_bottom_navigation")
                        ) {
                            // 1. Home
                            NavigationBarItem(
                                selected = currentScreen == Screen.HOME,
                                onClick = { viewModel.navigateTo(Screen.HOME) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Home",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentScreen == Screen.HOME) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandConfig.ACCENT_COLOR,
                                    selectedTextColor = BrandConfig.ACCENT_COLOR,
                                    unselectedIconColor = BrandConfig.MUTED_TEXT,
                                    unselectedTextColor = BrandConfig.MUTED_TEXT,
                                    indicatorColor = BrandConfig.PRIMARY_COLOR.copy(alpha = 0.2f)
                                )
                            )

                            // 2. Cari
                            NavigationBarItem(
                                selected = currentScreen == Screen.SEARCH,
                                onClick = { viewModel.navigateTo(Screen.SEARCH) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == Screen.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                        contentDescription = "Cari",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Cari",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentScreen == Screen.SEARCH) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandConfig.ACCENT_COLOR,
                                    selectedTextColor = BrandConfig.ACCENT_COLOR,
                                    unselectedIconColor = BrandConfig.MUTED_TEXT,
                                    unselectedTextColor = BrandConfig.MUTED_TEXT,
                                    indicatorColor = BrandConfig.PRIMARY_COLOR.copy(alpha = 0.2f)
                                )
                            )

                            // 3. Lainnya
                            NavigationBarItem(
                                selected = currentScreen == Screen.MORE,
                                onClick = { viewModel.navigateTo(Screen.MORE) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == Screen.MORE) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                                        contentDescription = "Lainnya",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Lainnya",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentScreen == Screen.MORE) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandConfig.ACCENT_COLOR,
                                    selectedTextColor = BrandConfig.ACCENT_COLOR,
                                    unselectedIconColor = BrandConfig.MUTED_TEXT,
                                    unselectedTextColor = BrandConfig.MUTED_TEXT,
                                    indicatorColor = BrandConfig.PRIMARY_COLOR.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.HOME -> {
                        val homeData by viewModel.homeData.collectAsState()
                        val isHomeLoading by viewModel.isHomeLoading.collectAsState()

                        HomeScreen(
                            homeData = homeData,
                            isLoading = isHomeLoading,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                            onArtistClick = { artist ->
                                viewModel.onSearchQueryChange(artist.name)
                                viewModel.navigateTo(Screen.SEARCH)
                            },
                            onAlbumClick = { album ->
                                viewModel.onSearchQueryChange(album.title)
                                viewModel.navigateTo(Screen.SEARCH)
                            }
                        )
                    }

                    Screen.SEARCH -> {
                        val searchQuery by viewModel.searchQuery.collectAsState()
                        val searchResults by viewModel.searchResults.collectAsState()
                        val isSearching by viewModel.isSearching.collectAsState()

                        SearchScreen(
                            searchQuery = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            searchResults = searchResults,
                            isSearching = isSearching,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) }
                        )
                    }

                    Screen.MORE -> {
                        val cacheSize by viewModel.cacheSize.collectAsState()
                        val cacheStats by viewModel.cacheStats.collectAsState()

                        MoreScreen(
                            cacheSize = cacheSize,
                            cacheStats = cacheStats,
                            likedCount = likedTracks.size,
                            downloadCount = downloadedTracks.size,
                            onNavigateLiked = { viewModel.navigateTo(Screen.LIKED) },
                            onNavigateDownloads = { viewModel.navigateTo(Screen.DOWNLOADS) },
                            onNavigateTop50 = {
                                viewModel.onSearchQueryChange("Top 50 Global")
                                viewModel.navigateTo(Screen.SEARCH)
                            },
                            onNavigatePlaylists = { viewModel.navigateTo(Screen.LIKED) },
                            onNavigateUploads = { viewModel.navigateTo(Screen.DOWNLOADS) },
                            onNavigateShazam = { viewModel.navigateTo(Screen.SHAZAM) },
                            onNavigateLastFm = { viewModel.navigateTo(Screen.LASTFM) },
                            onNavigateSettings = { viewModel.navigateTo(Screen.SETTINGS) },
                            onClearCache = { viewModel.clearCache() }
                        )
                    }

                    Screen.SHAZAM -> {
                        val isListening by viewModel.isShazamListening.collectAsState()
                        val shazamResult by viewModel.shazamResult.collectAsState()

                        ShazamScreen(
                            isListening = isListening,
                            recognizedResult = shazamResult,
                            onStartListening = { viewModel.startShazamRecognition() },
                            onPlayTrack = { track -> viewModel.playTrack(track) },
                            onAddToPlaylist = { track -> viewModel.toggleLike(track) },
                            onBack = { viewModel.navigateTo(Screen.MORE) }
                        )
                    }

                    Screen.DOWNLOADS -> {
                        DownloadsScreen(
                            downloads = downloads,
                            downloadedTracks = downloadedTracks,
                            onPlayTrack = { track, list -> viewModel.playTrack(track, list) },
                            onPause = { viewModel.downloadManager.pauseDownload(it) },
                            onResume = { viewModel.downloadManager.resumeDownload(it) },
                            onCancel = { viewModel.downloadManager.cancelDownload(it) },
                            onRetry = { viewModel.downloadManager.retryDownload(it) },
                            onBack = { viewModel.navigateTo(Screen.MORE) }
                        )
                    }

                    Screen.LIKED -> {
                        LikedTracksScreen(
                            likedTracks = likedTracks,
                            onPlayTrack = { track, list -> viewModel.playTrack(track, list) },
                            onUnlike = { viewModel.toggleLike(it) },
                            onBack = { viewModel.navigateTo(Screen.MORE) }
                        )
                    }

                    Screen.LASTFM -> {
                        val lastFmUsername by viewModel.lastFmUsername.collectAsState()
                        val isScrobbleEnabled by viewModel.lastFmScrobbleEnabled.collectAsState()
                        val scrobbleHistory by viewModel.lastFmScrobbler.scrobbleHistory.collectAsState()

                        LastFmScreen(
                            username = lastFmUsername,
                            isScrobbleEnabled = isScrobbleEnabled,
                            scrobbleHistory = scrobbleHistory,
                            onSaveUsername = { viewModel.setLastFmUsername(it) },
                            onToggleScrobble = { viewModel.setLastFmScrobble(it) },
                            onBack = { viewModel.navigateTo(Screen.MORE) }
                        )
                    }

                    Screen.SETTINGS -> {
                        val darkMode by viewModel.darkMode.collectAsState()
                        val audioQuality by viewModel.audioQuality.collectAsState()
                        val downloadQuality by viewModel.downloadQuality.collectAsState()
                        val lyricsSource by viewModel.lyricsSource.collectAsState()

                        SettingsScreen(
                            darkMode = darkMode,
                            audioQuality = audioQuality,
                            downloadQuality = downloadQuality,
                            lyricsSource = lyricsSource,
                            onToggleDarkMode = { viewModel.setDarkMode(it) },
                            onSelectAudioQuality = { viewModel.setAudioQuality(it) },
                            onSelectDownloadQuality = { viewModel.setDownloadQuality(it) },
                            onSelectLyricsSource = { viewModel.setLyricsSource(it) },
                            onClearCache = { viewModel.clearCache() },
                            onBack = { viewModel.navigateTo(Screen.MORE) }
                        )
                    }
                }
            }
        }

        // Full Player Modal Overlay
        FullPlayer(
            visible = isFullPlayerVisible,
            track = currentTrack,
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            isShuffle = isShuffle,
            isRepeat = isRepeat,
            queue = queue,
            lyricsData = currentLyrics,
            isLyricsLoading = isLyricsLoading,
            onDismiss = { viewModel.hideFullPlayer() },
            onPlayPause = { viewModel.togglePlayPause() },
            onSeek = { viewModel.seekTo(it) },
            onNext = { viewModel.playNext() },
            onPrevious = { viewModel.playPrevious() },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onToggleRepeat = { viewModel.toggleRepeat() },
            onToggleLike = { viewModel.toggleLike(it) },
            onDownload = { viewModel.downloadTrack(it) },
            onQueueTrackClick = { viewModel.playTrack(it) }
        )
    }
}
