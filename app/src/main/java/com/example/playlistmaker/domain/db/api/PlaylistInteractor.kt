package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun savePlaylist(playlist: Playlist): Flow<Unit>
}