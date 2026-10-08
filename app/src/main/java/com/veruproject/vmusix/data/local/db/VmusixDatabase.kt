package com.veruproject.vmusix.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.veruproject.vmusix.data.local.db.dao.HistoryDao
import com.veruproject.vmusix.data.local.db.dao.LyricsDao
import com.veruproject.vmusix.data.local.db.dao.PlaylistDao
import com.veruproject.vmusix.data.local.db.dao.TrackDao
import com.veruproject.vmusix.data.local.db.entity.HistoryEntity
import com.veruproject.vmusix.data.local.db.entity.LyricsEntity
import com.veruproject.vmusix.data.local.db.entity.PlaylistEntity
import com.veruproject.vmusix.data.local.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        HistoryEntity::class,
        LyricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VmusixDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun lyricsDao(): LyricsDao

    companion object {
        @Volatile
        private var INSTANCE: VmusixDatabase? = null

        fun getInstance(context: Context): VmusixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VmusixDatabase::class.java,
                    "vmusix_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
