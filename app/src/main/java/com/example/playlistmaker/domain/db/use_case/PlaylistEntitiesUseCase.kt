package com.example.playlistmaker.domain.db.use_case

import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow

class PlaylistEntitiesUseCase(
    private val playlistRepository: PlaylistRepository
) {
    fun executeSavePlaylist(playlist: Playlist): Flow<Unit> {
        return playlistRepository.savePlaylist(playlist)
    }

    fun executeGetPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    fun executeGetPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    fun executeGetTracks(): Flow<List<Track>> {
        return playlistRepository.getTracks()
    }

    fun executeGetTracksFromPlaylist(idsList: List<Long>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(idsList)
    }

    fun executeUpdatePlaylist(playlist: Playlist, track: Track): Flow<Unit> {
        return playlistRepository.updatePlaylist(playlist, track)
    }
}