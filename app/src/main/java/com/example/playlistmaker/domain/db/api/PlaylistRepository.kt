package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun savePlaylist(playlist: Playlist): Flow<Unit>

    fun updatePlaylist(playlist: Playlist): Flow<Unit>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Long): Flow<Playlist?>

    fun deletePlaylist(playlist: Playlist): Flow<Unit>

    fun getTracks(): Flow<List<Track>>

    fun getTracksFromPlaylist(list: List<Long>): Flow<List<Track>>

    fun deleteTrack(track: Track): Flow<Unit>

    fun addTrackToPlaylist(playlist: Playlist, track: Track): Flow<Unit>

    fun deleteTrackFromPlaylist(playlist: Playlist, track: Track): Flow<Unit>
}