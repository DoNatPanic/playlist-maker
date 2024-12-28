package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavouriteDao
import com.example.playlistmaker.data.db.dao.PlaylistDao

@Database(version = 2, entities = [FavouriteEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun playlistDao(): PlaylistDao
}