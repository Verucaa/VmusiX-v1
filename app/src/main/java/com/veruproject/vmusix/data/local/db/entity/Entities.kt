package com.veruproject.vmusix.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.veruproject.vmusix.domain.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val audioUrl: String,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val downloadDate: Long = 0,
    val addedDate: Long = System.currentTimeMillis()
) {
    fun toTrack(): Track = Track(
        id = id,
        title = title,
        artist = artist,
        album = album,
        durationSeconds = durationSeconds,
        thumbnailUrl = thumbnailUrl,
        audioUrl = audioUrl,
        isLiked = isLiked,
        isDownloaded = isDownloaded,
        localFilePath = localFilePath
    )

    companion object {
        fun fromTrack(track: Track): TrackEntity = TrackEntity(
            id = track.id,
            title = track.title,
            artist = track.artist,
            album = track.album,
            durationSeconds = track.durationSeconds,
            thumbnailUrl = track.thumbnailUrl,
            audioUrl = track.audioUrl,
            isLiked = track.isLiked,
            isDownloaded = track.isDownloaded,
            localFilePath = track.localFilePath
        )
    }
}

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val trackCount: Int,
    val thumbnailUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val trackId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_lyrics")
data class LyricsEntity(
    @PrimaryKey val trackId: String,
    val rawLrcContent: String,
    val provider: String,
    val isSynced: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)
