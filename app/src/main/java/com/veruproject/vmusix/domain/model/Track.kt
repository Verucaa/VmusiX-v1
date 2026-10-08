package com.veruproject.vmusix.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationSeconds: Long = 0,
    val thumbnailUrl: String = "",
    val audioUrl: String = "",
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val source: String = "YouTube Music"
) {
    val durationFormatted: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val year: String = "",
    val thumbnailUrl: String = "",
    val trackCount: Int = 0,
    val tracks: List<Track> = emptyList()
)

data class Artist(
    val id: String,
    val name: String,
    val thumbnailUrl: String = "",
    val subscribers: String = ""
)

data class Playlist(
    val id: String,
    val title: String,
    val author: String = "Vmusix",
    val trackCount: Int = 0,
    val thumbnailUrl: String = "",
    val isCustom: Boolean = false,
    val tracks: List<Track> = emptyList()
)

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class LyricsData(
    val trackId: String,
    val lines: List<LyricLine> = emptyList(),
    val plainLyrics: String = "",
    val provider: String = "Unknown",
    val isSynced: Boolean = false
)

data class ShazamResult(
    val title: String,
    val artist: String,
    val album: String = "",
    val coverUrl: String = "",
    val matchConfidence: Float = 0.95f
)

data class DownloadItem(
    val id: String,
    val track: Track,
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val localPath: String? = null,
    val error: String? = null
)

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}
