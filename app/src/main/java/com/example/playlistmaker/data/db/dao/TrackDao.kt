package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackEntity

@Dao
interface TrackDao {
    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(trackEntity: TrackEntity)

    @Transaction
    fun updatePlaylist(playlistEntity: PlaylistEntity, trackEntity: TrackEntity) {
        updatePlaylist(playlistEntity)
        insertTrack(trackEntity)
    }

    @Delete(entity = TrackEntity::class)
    fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM tracks_table")
    fun getTracks(): List<TrackEntity>
}