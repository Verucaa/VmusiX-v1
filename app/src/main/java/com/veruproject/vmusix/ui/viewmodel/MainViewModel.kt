package com.veruproject.vmusix.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.veruproject.vmusix.data.cache.CacheManager
import com.veruproject.vmusix.data.cache.CacheStats
import com.veruproject.vmusix.data.download.DownloadManager
import com.veruproject.vmusix.data.local.db.VmusixDatabase
import com.veruproject.vmusix.data.local.db.entity.TrackEntity
import com.veruproject.vmusix.data.local.prefs.UserPreferences
import com.veruproject.vmusix.data.remote.HomeData
import com.veruproject.vmusix.data.remote.YouTubeMusicService
import com.veruproject.vmusix.data.remote.lastfm.LastFmScrobbler
import com.veruproject.vmusix.data.remote.lyrics.LyricsRepository
import com.veruproject.vmusix.data.remote.shazam.ShazamRecognizer
import com.veruproject.vmusix.domain.model.Album
import com.veruproject.vmusix.domain.model.DownloadItem
import com.veruproject.vmusix.domain.model.LyricsData
import com.veruproject.vmusix.domain.model.ShazamResult
import com.veruproject.vmusix.domain.model.Track
import com.veruproject.vmusix.playback.MusicPlayer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    SEARCH,
    MORE,
    SHAZAM,
    DOWNLOADS,
    LIKED,
    LASTFM,
    SETTINGS
}

@OptIn(FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VmusixDatabase.getInstance(application)
    private val trackDao = db.trackDao()
    private val lyricsDao = db.lyricsDao()

    val userPreferences = UserPreferences(application)
    val musicPlayer = MusicPlayer(application, viewModelScope)
    private val ytMusicService = YouTubeMusicService()
    val lyricsRepository = LyricsRepository(lyricsDao)
    val shazamRecognizer = ShazamRecognizer()
    val lastFmScrobbler = LastFmScrobbler()
    val downloadManager = DownloadManager(application, trackDao, viewModelScope)
    val cacheManager = CacheManager(application, lyricsDao)

    // Navigation State
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isFullPlayerVisible = MutableStateFlow(false)
    val isFullPlayerVisible: StateFlow<Boolean> = _isFullPlayerVisible.asStateFlow()

    // Home Data State
    private val _homeData = MutableStateFlow<HomeData?>(null)
    val homeData: StateFlow<HomeData?> = _homeData.asStateFlow()

    private val _isHomeLoading = MutableStateFlow(true)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Lyrics State
    private val _currentLyrics = MutableStateFlow<LyricsData?>(null)
    val currentLyrics: StateFlow<LyricsData?> = _currentLyrics.asStateFlow()

    private val _isLyricsLoading = MutableStateFlow(false)
    val isLyricsLoading: StateFlow<Boolean> = _isLyricsLoading.asStateFlow()

    // Cache Stats
    private val _cacheSize = MutableStateFlow("0 B")
    val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

    private val _cacheStats = MutableStateFlow<CacheStats?>(null)
    val cacheStats: StateFlow<CacheStats?> = _cacheStats.asStateFlow()

    // Shazam State
    private val _isShazamListening = MutableStateFlow(false)
    val isShazamListening: StateFlow<Boolean> = _isShazamListening.asStateFlow()

    private val _shazamResult = MutableStateFlow<ShazamResult?>(null)
    val shazamResult: StateFlow<ShazamResult?> = _shazamResult.asStateFlow()

    // Database Liked and Downloaded flows
    val likedTracks: StateFlow<List<Track>> = trackDao.getLikedTracks()
        .map { list -> list.map { it.toTrack() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val downloadedTracks: StateFlow<List<Track>> = trackDao.getDownloadedTracks()
        .map { list -> list.map { it.toTrack() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val downloads: StateFlow<Map<String, DownloadItem>> = downloadManager.downloads

    // Settings preferences flows
    val darkMode = userPreferences.darkModeFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val audioQuality = userPreferences.audioQualityFlow.stateIn(viewModelScope, SharingStarted.Lazily, "Tinggi (320kbps)")
    val downloadQuality = userPreferences.downloadQualityFlow.stateIn(viewModelScope, SharingStarted.Lazily, "Tinggi (320kbps)")
    val lyricsSource = userPreferences.lyricsSourceFlow.stateIn(viewModelScope, SharingStarted.Lazily, "Auto (Cascade)")
    val lastFmUsername = userPreferences.lastFmUsernameFlow.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val lastFmScrobbleEnabled = userPreferences.lastFmScrobbleEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        loadHomeData()
        refreshCacheStats()
        setupSearchDebounce()
        setupPlaybackTrackObserver()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _isHomeLoading.value = true
            try {
                val data = ytMusicService.getHomeFeeds()
                _homeData.value = data
            } finally {
                _isHomeLoading.value = false
            }
        }
    }

    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    _isSearching.value = true
                    try {
                        val results = ytMusicService.search(query)
                        _searchResults.value = results
                    } finally {
                        _isSearching.value = false
                    }
                } else {
                    _searchResults.value = emptyList()
                    _isSearching.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setupPlaybackTrackObserver() {
        musicPlayer.currentTrack.onEach { track ->
            if (track != null) {
                loadLyricsForTrack(track)
                // Scrobble now playing to Last.fm if enabled
                if (lastFmScrobbleEnabled.value && lastFmUsername.value.isNotBlank()) {
                    lastFmScrobbler.updateNowPlaying(track, lastFmUsername.value)
                }
            } else {
                _currentLyrics.value = null
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _isSearching.value = true
        }
    }

    fun playTrack(track: Track, queue: List<Track> = emptyList()) {
        viewModelScope.launch {
            // Check if track is liked in local DB
            val localTrack = trackDao.getTrackById(track.id)
            val effectiveTrack = if (localTrack != null) {
                track.copy(
                    isLiked = localTrack.isLiked,
                    isDownloaded = localTrack.isDownloaded,
                    localFilePath = localTrack.localFilePath
                )
            } else {
                track
            }
            musicPlayer.playTrack(effectiveTrack, queue)
        }
    }

    fun togglePlayPause() = musicPlayer.togglePlayPause()
    fun playNext() = musicPlayer.playNext()
    fun playPrevious() = musicPlayer.playPrevious()
    fun seekTo(positionMs: Long) = musicPlayer.seekTo(positionMs)
    fun toggleShuffle() = musicPlayer.toggleShuffle()
    fun toggleRepeat() = musicPlayer.toggleRepeat()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun showFullPlayer() {
        _isFullPlayerVisible.value = true
    }

    fun hideFullPlayer() {
        _isFullPlayerVisible.value = false
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch {
            val isNowLiked = !track.isLiked
            val entity = TrackEntity.fromTrack(track).copy(
                isLiked = isNowLiked,
                addedDate = System.currentTimeMillis()
            )
            trackDao.insertOrUpdate(entity)
            if (musicPlayer.currentTrack.value?.id == track.id) {
                // update state
                musicPlayer.exoPlayer.currentMediaItem?.let {
                    // updated
                }
            }
        }
    }

    fun downloadTrack(track: Track) {
        downloadManager.downloadTrack(track)
    }

    fun downloadAlbum(album: Album) {
        downloadManager.downloadAlbum(album.tracks)
    }

    fun loadLyricsForTrack(track: Track) {
        viewModelScope.launch {
            _isLyricsLoading.value = true
            try {
                val data = lyricsRepository.getLyrics(track)
                _currentLyrics.value = data
            } finally {
                _isLyricsLoading.value = false
            }
        }
    }

    fun startShazamRecognition() {
        viewModelScope.launch {
            _isShazamListening.value = true
            _shazamResult.value = null
            try {
                val result = shazamRecognizer.recognizeAudio()
                result.onSuccess {
                    _shazamResult.value = it
                }
            } finally {
                _isShazamListening.value = false
            }
        }
    }

    fun refreshCacheStats() {
        viewModelScope.launch {
            _cacheSize.value = cacheManager.getCacheSizeFormatted()
            _cacheStats.value = cacheManager.getCacheStatistics()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            cacheManager.clearCache()
            refreshCacheStats()
        }
    }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { userPreferences.setDarkMode(enabled) }
    fun setAudioQuality(q: String) = viewModelScope.launch { userPreferences.setAudioQuality(q) }
    fun setDownloadQuality(q: String) = viewModelScope.launch { userPreferences.setDownloadQuality(q) }
    fun setLyricsSource(s: String) = viewModelScope.launch { userPreferences.setLyricsSource(s) }
    fun setLastFmUsername(u: String) = viewModelScope.launch { userPreferences.setLastFmUsername(u) }
    fun setLastFmScrobble(enabled: Boolean) = viewModelScope.launch { userPreferences.setLastFmScrobbleEnabled(enabled) }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
    }
}
