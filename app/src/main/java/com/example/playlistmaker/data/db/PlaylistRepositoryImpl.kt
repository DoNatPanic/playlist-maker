package com.example.playlistmaker.data.db

import android.util.Log
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

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        var list = getRequest()
        emit(convertToPlaylist(list))
    }

    private suspend fun insertRequest(playlistEntity: PlaylistEntity) {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.playlistDao().insertPlaylist(playlistEntity)
            } catch (e: Throwable) {
                Log.i("some err", "error")
            }
        }
    }

    private suspend fun getRequest(): List<PlaylistEntity> {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.playlistDao().getPlaylists()
            } catch (e: Throwable) {
                listOf()
            }
        }
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertToPlaylist(playlistEntities: List<PlaylistEntity>): List<Playlist> {
        return playlistEntities.map { item -> playlistDbConvertor.map(item) }
    }
}
