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

                    // из строки идентификаторов треков этого плейлиста сделали список
                    val list = createIdsListFromJson(result.trackIds!!)

                    // преобразуем тип в Long
                    var idsList = mutableListOf<Long>()
                    for (item in list) {
                        idsList.add(item.toLong())
                    }

                    // определяем список треков, принадлежащих плейлисту
                    getPlaylistTracks(idsList)
                }
            }
        }
    }

    private fun getPlaylistTracks(idsList: List<Long>) {
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetTracksFromPlaylist(idsList).collect { result ->
                if (result != null && result.isNotEmpty()) {
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