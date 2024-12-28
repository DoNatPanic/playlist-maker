package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override fun savePlaylist(playlist: Playlist): Flow<Unit> = flow {
        val playlistEntity = convertToPlaylistEntity(playlist)
        emit(insertRequest(playlistEntity))
    }

    private suspend fun insertRequest(playlistEntity: PlaylistEntity) {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.playlistDao().insertPlaylist(playlistEntity)
            } catch (e: Throwable) {
                // nothing
            }
        }
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }
}