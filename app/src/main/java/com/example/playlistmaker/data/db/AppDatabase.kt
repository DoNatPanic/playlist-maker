package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavouriteDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao

@Database(
    version = 3,
    entities = [FavouriteEntity::class, PlaylistEntity::class, TrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
}