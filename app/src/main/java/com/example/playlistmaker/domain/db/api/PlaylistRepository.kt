package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun savePlaylist(playlist: Playlist): Flow<Unit>

    fun getPlaylists(): Flow<List<Playlist>>
}