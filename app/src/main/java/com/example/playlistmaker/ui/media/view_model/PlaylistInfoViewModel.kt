package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.use_case.PlaylistEntitiesUseCase
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.presentation.utils.SingleEventLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistEntitiesUseCase: PlaylistEntitiesUseCase,
    private val gson: Gson
) : ViewModel() {

    private var playlists: List<Playlist> = mutableListOf()

    private val getPlaylistData = SingleEventLiveData<Playlist>()
    fun getPlaylistLiveData(): SingleEventLiveData<Playlist> = getPlaylistData

    private val getPlaylistTracksData = MutableLiveData<List<Track>>(listOf())
    fun getPlaylistTracksLiveData(): MutableLiveData<List<Track>> = getPlaylistTracksData

    fun getPlaylistFromDB(playlistId: Long) {
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetPlaylistById(playlistId).collect { result ->
                if (result != null) {

                    // по идентификатору плейлиста нашли плейлист в БД
                    getPlaylistData.postValue(result!!)

                    if (!result.trackIds.isNullOrEmpty()) {
                        // из строки идентификаторов треков этого плейлиста делаем список
                        var list: List<Long> = getIdsFromString(result.trackIds)

                        // запрашиваем список треков (по айдишникам), принадлежащих плейлисту
                        getPlaylistTracks(list)
                    }
                }
            }
        }
    }

    private fun getIdsFromString(str: String): List<Long> {
        var list: MutableList<String> = createIdsListFromJson(str)
        // преобразуем тип в Long
        var idsList = mutableListOf<Long>()
        for (item in list) {
            idsList.add(item.toLong())
        }
        return idsList
    }

    // пользователь хочет удалить песню из списка
    fun onDeleteTrackClicked(playlistId: Long, track: Track): Boolean {

        viewModelScope.launch {
            getPlaylistData.value?.let {
                playlistEntitiesUseCase.executeDeleteTrackFromPlaylist(it, track)
                    .collect { result ->
                        if (result != null) {
                            // get updated playlist info
                            getPlaylistFromDB(playlistId)
                            getPlaylists(track)
                        }
                    }
            }
        }
        return true
    }

    // берем из базы список всех плейлистов
    private fun getPlaylists(track: Track) {
        viewModelScope.launch {
            getPlaylistData.value?.let {
                playlistEntitiesUseCase.executeGetPlaylists().collect { result ->
                    var isExists = false

                    // ищем в плейлистах данный трек
                    if (result != null && result.isNotEmpty()) {
                        playlists = result
                        for (playlist in playlists) {
                            if (!playlist.trackIds.isNullOrEmpty()) {
                                val list = getIdsFromString(playlist.trackIds)
                                for (id in list) {
                                    if (id == track.trackId) {
                                        isExists = true
                                        break
                                    }
                                }
                            }
                        }
                    }
                    // если ни в одном плейлисте нет указанного трека, удаляем его из базы
                    if (!isExists) {
                        deleteTrackFromTable(track)
                    }
                }
            }
        }
    }

    private fun deleteTrackFromTable(track: Track) {
        viewModelScope.launch {
            getPlaylistData.value?.let {
                playlistEntitiesUseCase.executeDeleteTrack(track).collect {}
            }
        }
    }


    private fun getPlaylistTracks(idsList: List<Long>) {
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetTracksFromPlaylist(idsList).collect { result ->
                if (result != null) {
                    getPlaylistTracksData.postValue(result)
                }
            }
        }
    }

    private fun createIdsListFromJson(json: String): MutableList<String> {
        val mutableListTutorialType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, mutableListTutorialType)
    }
}