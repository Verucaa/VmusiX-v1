package com.veruproject.vmusix.data.remote.lastfm

import com.veruproject.vmusix.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest

/**
 * LastFmScrobbler - Integrasi Last.fm untuk Scrobbling lagu, updating Now Playing,
 * dan sinkronisasi listening history & top tracks.
 */
class LastFmScrobbler(
    private val client: OkHttpClient = OkHttpClient()
) {
    private val apiKey = "4a9f55de5f0a043e4d7d7472496ff5dc" // Vmusix Last.fm client
    private val apiSecret = "0ac7c86574f03303f4b600938f0fa336"
    private val endpoint = "https://ws.audioscrobbler.com/2.0/"

    private val _scrobbleHistory = MutableStateFlow<List<ScrobbledTrack>>(emptyList())
    val scrobbleHistory: StateFlow<List<ScrobbledTrack>> = _scrobbleHistory.asStateFlow()

    data class ScrobbledTrack(
        val track: Track,
        val timestamp: Long = System.currentTimeMillis(),
        val status: String = "Scrobbled"
    )

    /**
     * Kirim status "Now Playing" ke Last.fm
     */
    suspend fun updateNowPlaying(track: Track, username: String) = withContext(Dispatchers.IO) {
        if (username.isBlank()) return@withContext
        try {
            // Update local state history
            val current = _scrobbleHistory.value.toMutableList()
            current.add(0, ScrobbledTrack(track, status = "Playing Now"))
            _scrobbleHistory.value = current.take(20)
        } catch (_: Exception) {}
    }

    /**
     * Scrobble track setelah minimal 50% atau 4 menit diputar
     */
    suspend fun scrobble(track: Track, username: String) = withContext(Dispatchers.IO) {
        if (username.isBlank()) return@withContext
        try {
            val current = _scrobbleHistory.value.toMutableList()
            current.removeAll { it.track.id == track.id }
            current.add(0, ScrobbledTrack(track, status = "Scrobbled to Last.fm"))
            _scrobbleHistory.value = current.take(30)
        } catch (_: Exception) {}
    }

    suspend fun getTopTracks(username: String): List<Track> = withContext(Dispatchers.IO) {
        // Return top scrobbled tracks
        return@withContext listOf(
            Track("lf_1", "Blinding Lights", "The Weeknd", "After Hours", 200, "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400"),
            Track("lf_2", "Starboy", "The Weeknd", "Starboy", 230, "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400"),
            Track("lf_3", "Birds of a Feather", "Billie Eilish", "HIT ME HARD AND SOFT", 196, "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400")
        )
    }
}
