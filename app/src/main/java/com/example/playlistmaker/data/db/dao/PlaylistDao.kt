package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Update(entity = PlaylistEntity::class)
    fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE playlist_id = :id")
    fun getPlaylistById(id: Long): PlaylistEntity

    @Delete(entity = PlaylistEntity::class)
    fun deletePlaylist(playlistEntity: PlaylistEntity)
}