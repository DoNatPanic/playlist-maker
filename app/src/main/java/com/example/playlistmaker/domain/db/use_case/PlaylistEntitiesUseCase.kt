package com.example.playlistmaker.domain.db.use_case

import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistEntitiesUseCase(
    private val playlistRepository: PlaylistRepository
) {
    fun executeSave(playlist: Playlist): Flow<Unit> {
        return playlistRepository.savePlaylist(playlist)
    }

    fun executeGet(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }
}