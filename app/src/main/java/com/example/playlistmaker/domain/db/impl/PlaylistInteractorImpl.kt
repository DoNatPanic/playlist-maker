package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override fun savePlaylist(playlist: Playlist): Flow<Unit> = flow {
        playlistRepository.savePlaylist(playlist)
    }
}