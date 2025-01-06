package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.converters.TrackDbConverter
import com.example.playlistmaker.data.search.converters.TrackConverter
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackConverter: TrackConverter,
    private val trackDbConverter: TrackDbConverter,
    private val gson: Gson,
) : PlaylistRepository {
    override fun savePlaylist(playlist: Playlist): Flow<Unit> = flow {
        val playlistEntity = convertToPlaylistEntity(playlist)
        emit(insertPlaylistRequest(playlistEntity))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        var list = getPlaylistsRequest()
        emit(convertToPlaylist(list))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        var list = getTracksRequest()
        emit(convertToTracksEntity(list))
    }

    override fun updatePlaylist(playlist: Playlist, track: Track): Flow<Unit> = flow {
        val trackEntity = convertToTrackEntity(track)
        val playlistEntity = convertToPlaylistEntity(playlist)
        emit(updatePlaylistRequest(playlistEntity, trackEntity))
    }

    private suspend fun insertPlaylistRequest(playlistEntity: PlaylistEntity) {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.playlistDao().insertPlaylist(playlistEntity)
            } catch (e: Throwable) {
                // nothing
            }
        }
    }

    private suspend fun updatePlaylistRequest(
        playlistEntity: PlaylistEntity,
        trackEntity: TrackEntity
    ) {
        return withContext(Dispatchers.IO) {
            try {
                var list: MutableList<String> = mutableListOf()
                if (!playlistEntity.trackIds.isNullOrEmpty()) {
                    list.addAll(createIdsListFromJson(playlistEntity.trackIds!!))
                }
                list.add(trackEntity.trackId.toString())

                var newPlaylistEntity = PlaylistEntity(
                    playlistId = playlistEntity.playlistId,
                    tracksCount = playlistEntity.tracksCount + 1,
                    playlistName = playlistEntity.playlistName,
                    playlistInfo = playlistEntity.playlistInfo,
                    playlistImgPath = playlistEntity.playlistImgPath,
                    trackIds = createJsonFromIdsList(list)
                )
                appDatabase.trackDao().updatePlaylist(newPlaylistEntity, trackEntity)
            } catch (e: Throwable) {
                // nothing
            }
        }
    }

    private suspend fun getPlaylistsRequest(): List<PlaylistEntity> {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.playlistDao().getPlaylists()
            } catch (e: Throwable) {
                listOf()
            }
        }
    }

    private suspend fun getTracksRequest(): List<TrackEntity> {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.trackDao().getTracks()
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

    private fun convertToTrackEntity(track: Track): TrackEntity {
        val trackDto = trackConverter.map(track)
        return trackDbConverter.map(trackDto)
    }

    private fun convertToTracksEntity(list: List<TrackEntity>): List<Track> {
        return list.map { track -> trackDbConverter.map(track) }
    }

    private fun createJsonFromIdsList(trackId: List<String>): String {
        return gson.toJson(trackId)
    }

    private fun createIdsListFromJson(json: String): MutableList<String> {
        val mutableListTutorialType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, mutableListTutorialType)
    }
}
