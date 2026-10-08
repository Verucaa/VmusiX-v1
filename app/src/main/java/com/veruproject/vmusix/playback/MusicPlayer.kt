package com.veruproject.vmusix.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.veruproject.vmusix.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * MusicPlayer - Core Media3 ExoPlayer Controller untuk Vmusix.
 * Mengelola state pemutaran lagu, antrean, posisi waktu real-time, shuffle, dan repeat.
 */
class MusicPlayer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            addListener(playerListener)
        }
    }

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                startProgressTracker()
            } else {
                progressJob?.cancel()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = exoPlayer.duration.coerceAtLeast(0L)
            } else if (playbackState == Player.STATE_ENDED) {
                playNext()
            }
        }
    }

    fun playTrack(track: Track, newQueue: List<Track> = emptyList()) {
        _currentTrack.value = track
        if (newQueue.isNotEmpty()) {
            _queue.value = newQueue
        } else if (!_queue.value.any { it.id == track.id }) {
            _queue.value = listOf(track) + _queue.value
        }

        // Resolusi URI: prioritaskan file lokal yang sudah diunduh
        val uri = if (!track.localFilePath.isNullOrBlank()) {
            Uri.parse(track.localFilePath)
        } else {
            Uri.parse(track.audioUrl)
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)
            .setArtworkUri(Uri.parse(track.thumbnailUrl))
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(uri)
            .setMediaMetadata(metadata)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun playNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val currentIndex = q.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIndex != -1 && currentIndex + 1 < q.size) {
            playTrack(q[currentIndex + 1])
        } else if (_isRepeat.value && q.isNotEmpty()) {
            playTrack(q[0])
        }
    }

    fun playPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        val currentIndex = q.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIndex > 0) {
            playTrack(q[currentIndex - 1])
        } else {
            seekTo(0L)
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
        if (_isShuffle.value) {
            val current = _currentTrack.value
            val shuffled = _queue.value.shuffled().toMutableList()
            if (current != null) {
                shuffled.remove(current)
                shuffled.add(0, current)
            }
            _queue.value = shuffled
        }
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
    }

    fun addToQueue(track: Track) {
        val current = _queue.value.toMutableList()
        if (!current.any { it.id == track.id }) {
            current.add(track)
            _queue.value = current
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _currentPosition.value = exoPlayer.currentPosition
                    _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                }
                delay(300)
            }
        }
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }
}
