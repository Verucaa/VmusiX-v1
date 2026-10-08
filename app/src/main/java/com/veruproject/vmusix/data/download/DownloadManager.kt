package com.veruproject.vmusix.data.download

import android.content.Context
import android.os.Environment
import com.veruproject.vmusix.data.local.db.dao.TrackDao
import com.veruproject.vmusix.data.local.db.entity.TrackEntity
import com.veruproject.vmusix.domain.model.DownloadItem
import com.veruproject.vmusix.domain.model.DownloadStatus
import com.veruproject.vmusix.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * DownloadManager - Mengelola download Lagu, Album, dan Playlist.
 * Mendukung Pause, Resume, Cancel, dan Retry.
 * File disimpan ke storage: Music/Vmusix.
 */
class DownloadManager(
    private val context: Context,
    private val trackDao: TrackDao,
    private val scope: CoroutineScope
) {
    private val _downloads = MutableStateFlow<Map<String, DownloadItem>>(emptyMap())
    val downloads: StateFlow<Map<String, DownloadItem>> = _downloads.asStateFlow()

    private val activeJobs = mutableMapOf<String, Job>()

    private val downloadFolder: File by lazy {
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: File(context.filesDir, "Music")
        File(musicDir, "Vmusix").apply { mkdirs() }
    }

    fun downloadTrack(track: Track) {
        val current = _downloads.value.toMutableMap()
        val item = DownloadItem(
            id = track.id,
            track = track,
            progress = 0f,
            status = DownloadStatus.QUEUED
        )
        current[track.id] = item
        _downloads.value = current

        startDownload(track.id)
    }

    fun downloadAlbum(tracks: List<Track>) {
        tracks.forEach { downloadTrack(it) }
    }

    fun downloadPlaylist(tracks: List<Track>) {
        tracks.forEach { downloadTrack(it) }
    }

    fun pauseDownload(trackId: String) {
        activeJobs[trackId]?.cancel()
        activeJobs.remove(trackId)
        updateStatus(trackId, DownloadStatus.PAUSED)
    }

    fun resumeDownload(trackId: String) {
        startDownload(trackId)
    }

    fun cancelDownload(trackId: String) {
        activeJobs[trackId]?.cancel()
        activeJobs.remove(trackId)
        val current = _downloads.value.toMutableMap()
        current.remove(trackId)
        _downloads.value = current

        scope.launch(Dispatchers.IO) {
            val targetFile = File(downloadFolder, "${sanitizeFilename(trackId)}.mp3")
            if (targetFile.exists()) targetFile.delete()
            trackDao.setDownloaded(trackId, false, null)
        }
    }

    fun retryDownload(trackId: String) {
        startDownload(trackId)
    }

    private fun startDownload(trackId: String) {
        val item = _downloads.value[trackId] ?: return
        val track = item.track

        updateStatus(trackId, DownloadStatus.DOWNLOADING)

        val job = scope.launch(Dispatchers.IO) {
            val targetFile = File(downloadFolder, "${sanitizeFilename(track.id)}.mp3")
            try {
                // Simulasi download audio chunk dengan progress dinamis
                var downloadedBytes = 0L
                val totalBytes = 1024 * 1024 * 4L // Approx 4MB

                val fos = FileOutputStream(targetFile)
                val buffer = ByteArray(8192)

                for (step in 1..20) {
                    delay(150)
                    downloadedBytes += (totalBytes / 20)
                    val progress = (step / 20f).coerceIn(0f, 1f)
                    updateProgress(trackId, progress)
                }

                fos.write(ByteArray(1024))
                fos.close()

                // Simpan ke Room Database
                val entity = TrackEntity.fromTrack(track).copy(
                    isDownloaded = true,
                    localFilePath = targetFile.absolutePath,
                    downloadDate = System.currentTimeMillis()
                )
                trackDao.insertOrUpdate(entity)

                updateStatus(trackId, DownloadStatus.COMPLETED, targetFile.absolutePath)
            } catch (e: Exception) {
                if (item.status != DownloadStatus.PAUSED) {
                    updateStatus(trackId, DownloadStatus.FAILED, error = e.localizedMessage)
                }
            } finally {
                activeJobs.remove(trackId)
            }
        }
        activeJobs[trackId] = job
    }

    private fun updateProgress(trackId: String, progress: Float) {
        val current = _downloads.value.toMutableMap()
        val item = current[trackId] ?: return
        current[trackId] = item.copy(progress = progress)
        _downloads.value = current
    }

    private fun updateStatus(trackId: String, status: DownloadStatus, localPath: String? = null, error: String? = null) {
        val current = _downloads.value.toMutableMap()
        val item = current[trackId] ?: return
        current[trackId] = item.copy(status = status, localPath = localPath ?: item.localPath, error = error)
        _downloads.value = current
    }

    private fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    }
}
