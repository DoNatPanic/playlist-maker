package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlistEntity: PlaylistEntity)
}