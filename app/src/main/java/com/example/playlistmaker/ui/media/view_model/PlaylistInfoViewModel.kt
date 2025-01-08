package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.use_case.PlaylistEntitiesUseCase
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.sharing.api.SharingInteractor
import com.example.playlistmaker.presentation.utils.SingleEventLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoViewModel(
    private val playlistEntitiesUseCase: PlaylistEntitiesUseCase,
    private val sharingInteractor: SharingInteractor,
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

    // берем из базы список всех плейлистов ...
    private fun getPlaylists(track: Track) {
        // ... и ищем в них данный трек, если не найдем - удаляем его из таблицы
        compareWithAnotherPlaylistsTracks(track, null)
    }

    private fun compareWithAnotherPlaylistsTracks(track: Track, currentPlaylist: Playlist?) {
        viewModelScope.launch {
            getPlaylistData.value?.let {
                playlistEntitiesUseCase.executeGetPlaylists().collect { result ->
                    var isExists = false

                    // ищем в плейлистах данный трек
                    if (result != null && result.isNotEmpty()) {
                        playlists = result
                        for (playlist in playlists) {
                            if (currentPlaylist != null && playlist == currentPlaylist) break
//                            if (playlist != currentPlaylist) {
                            if (!playlist.trackIds.isNullOrEmpty()) {
                                val list = getIdsFromString(playlist.trackIds)
                                for (id in list) {
                                    if (id == track.trackId) {
                                        isExists = true
                                        break
                                    }
                                }
//                            }
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

    fun onShareButtonClicked(playlist: Playlist, trackList: List<Track>) {
        val playlist = playlist

        // добавили название плейлиста, описание, количество треков
        var message =
            "${playlist.playlistName}\n${playlist.playlistInfo}\n${makeMinutesText(playlist.tracksCount)}\n"

        // список треков (построчно) в формате:
        // [номер]. [имя исполнителя] - [название трека] ([продолжительность трека])
        var count = 0
        for (track in trackList) {
            message += "${++count}. ${track.artistName} - ${track.artistName} (${makeTimeText(track.trackTime)})\n"
        }
        sharingInteractor.sharePlaylist(message)
    }

    fun onDeletePlaylistClicked(playlist: Playlist, trackList: List<Track>) {
        // сначала сравниваем треки данного плейлиста с треками всех других плейлистов
        // и если их нет - удаляем этот трек из таблицы
        for (track in trackList) {
            compareWithAnotherPlaylistsTracks(track, playlist)
        }

        // после этого удаляем плейлист
        viewModelScope.launch {
            playlistEntitiesUseCase.executeDeletePlaylist(playlist).collect { }
        }
    }

    private fun makeTimeText(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    private fun makeMinutesText(tracksCount: Int): String {
        val num = tracksCount.toString()
        val chars = num.toList().reversed()
        var word = "трек"
        when (chars[0]) {
            '2', '3', '4' -> {
                word = "трека"
            }

            '0', '5', '6', '7', '8', '9' -> word = "треков"
        }
        if (chars.size > 1) {
            if ((chars[0] == '1' || chars[0] == '2' || chars[0] == '3' || chars[0] == '4') && chars[1] == '1') word =
                "треков"
        }

        return "$num $word"
    }
}