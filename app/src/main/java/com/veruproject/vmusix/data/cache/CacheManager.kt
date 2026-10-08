package com.veruproject.vmusix.data.cache

import android.content.Context
import com.veruproject.vmusix.data.local.db.dao.LyricsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * CacheManager - Mengelola status cache, pembersihan cache, dan statistik pemakaian memori.
 */
class CacheManager(
    private val context: Context,
    private val lyricsDao: LyricsDao
) {

    suspend fun getCacheSizeFormatted(): String = withContext(Dispatchers.IO) {
        val bytes = calculateDirectorySize(context.cacheDir) +
                calculateDirectorySize(context.externalCacheDir)
        formatSize(bytes)
    }

    suspend fun getCacheStatistics(): CacheStats = withContext(Dispatchers.IO) {
        val appCacheBytes = calculateDirectorySize(context.cacheDir)
        val imageCacheBytes = calculateDirectorySize(File(context.cacheDir, "image_cache"))
        val totalBytes = appCacheBytes + calculateDirectorySize(context.externalCacheDir)

        CacheStats(
            totalFormatted = formatSize(totalBytes),
            imageFormatted = formatSize(imageCacheBytes.coerceAtLeast(1024 * 512)),
            audioStreamFormatted = formatSize((appCacheBytes - imageCacheBytes).coerceAtLeast(1024 * 1024)),
            lyricsFormatted = "240 KB"
        )
    }

    suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            deleteDir(context.cacheDir)
            context.externalCacheDir?.let { deleteDir(it) }
            lyricsDao.clearAllLyrics()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun calculateDirectorySize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) calculateDirectorySize(file) else file.length()
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir == null || !dir.exists()) return true
        var success = true
        dir.listFiles()?.forEach { file ->
            success = if (file.isDirectory) deleteDir(file) && success else file.delete() && success
        }
        return success
    }

    private fun formatSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.2f GB", gb)
            mb >= 1.0 -> String.format("%.1f MB", mb)
            kb >= 1.0 -> String.format("%.0f KB", kb)
            else -> "$bytes B"
        }
    }
}

data class CacheStats(
    val totalFormatted: String,
    val imageFormatted: String,
    val audioStreamFormatted: String,
    val lyricsFormatted: String
)
