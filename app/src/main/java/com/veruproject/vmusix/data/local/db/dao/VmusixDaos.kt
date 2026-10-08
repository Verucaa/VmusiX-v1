package com.veruproject.vmusix.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.veruproject.vmusix.data.local.db.entity.HistoryEntity
import com.veruproject.vmusix.data.local.db.entity.LyricsEntity
import com.veruproject.vmusix.data.local.db.entity.PlaylistEntity
import com.veruproject.vmusix.data.local.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedDate DESC")
    fun getLikedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY downloadDate DESC")
    fun getDownloadedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(track: TrackEntity)

    @Update
    suspend fun update(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrack(id: String)

    @Query("UPDATE tracks SET isLiked = :isLiked WHERE id = :id")
    suspend fun setLiked(id: String, isLiked: Boolean)

    @Query("UPDATE tracks SET isDownloaded = :isDownloaded, localFilePath = :filePath WHERE id = :id")
    suspend fun setDownloaded(id: String, isDownloaded: Boolean, filePath: String?)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: String)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY playedAt DESC LIMIT 50")
    fun getHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}

@Dao
interface LyricsDao {
    @Query("SELECT * FROM cached_lyrics WHERE trackId = :trackId LIMIT 1")
    suspend fun getLyrics(trackId: String): LyricsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(lyrics: LyricsEntity)

    @Query("DELETE FROM cached_lyrics WHERE trackId = :trackId")
    suspend fun deleteLyrics(trackId: String)

    @Query("DELETE FROM cached_lyrics")
    suspend fun clearAllLyrics()
}
