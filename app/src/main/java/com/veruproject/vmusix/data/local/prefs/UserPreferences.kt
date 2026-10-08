package com.veruproject.vmusix.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vmusix_settings")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_AUDIO_QUALITY = stringPreferencesKey("audio_quality")
        val KEY_DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
        val KEY_LYRICS_SOURCE = stringPreferencesKey("lyrics_source")
        val KEY_LASTFM_USERNAME = stringPreferencesKey("lastfm_username")
        val KEY_LASTFM_SCROBBLE_ENABLED = booleanPreferencesKey("lastfm_scrobble_enabled")
        val KEY_AUTO_CLEAN_CACHE = booleanPreferencesKey("auto_clean_cache")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_MODE] ?: true }
    val audioQualityFlow: Flow<String> = context.dataStore.data.map { it[KEY_AUDIO_QUALITY] ?: "High (320kbps)" }
    val downloadQualityFlow: Flow<String> = context.dataStore.data.map { it[KEY_DOWNLOAD_QUALITY] ?: "High (320kbps)" }
    val lyricsSourceFlow: Flow<String> = context.dataStore.data.map { it[KEY_LYRICS_SOURCE] ?: "Auto (Cascade)" }
    val lastFmUsernameFlow: Flow<String> = context.dataStore.data.map { it[KEY_LASTFM_USERNAME] ?: "" }
    val lastFmScrobbleEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_LASTFM_SCROBBLE_ENABLED] ?: false }
    val autoCleanCacheFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_CLEAN_CACHE] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setAudioQuality(quality: String) {
        context.dataStore.edit { it[KEY_AUDIO_QUALITY] = quality }
    }

    suspend fun setDownloadQuality(quality: String) {
        context.dataStore.edit { it[KEY_DOWNLOAD_QUALITY] = quality }
    }

    suspend fun setLyricsSource(source: String) {
        context.dataStore.edit { it[KEY_LYRICS_SOURCE] = source }
    }

    suspend fun setLastFmUsername(username: String) {
        context.dataStore.edit { it[KEY_LASTFM_USERNAME] = username }
    }

    suspend fun setLastFmScrobbleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_LASTFM_SCROBBLE_ENABLED] = enabled }
    }

    suspend fun setAutoCleanCache(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_CLEAN_CACHE] = enabled }
    }
}
