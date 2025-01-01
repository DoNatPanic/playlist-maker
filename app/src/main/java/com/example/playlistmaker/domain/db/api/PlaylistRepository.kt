package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun savePlaylist(playlist: Playlist): Flow<Unit>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getTracks(): Flow<List<Track>>

    fun updatePlaylist(playlist: Playlist, track: Track): Flow<Unit>
}