package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.db.use_case.FavouriteEntitiesUseCase
import com.example.playlistmaker.domain.db.use_case.PlaylistEntitiesUseCase
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.entity.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    val track: Track,
    private val tracksInteractor: PlayerInteractor,
    private val favouriteEntitiesUseCase: FavouriteEntitiesUseCase,
    private val playlistEntitiesUseCase: PlaylistEntitiesUseCase,
    private val gson: Gson
) : ViewModel(), KoinComponent {

    private var timerJob: Job? = null

    private var isPlaying: Boolean = false

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val elapsedTimeState = MutableLiveData<Long>(0L)
    fun elapsedTimeLiveData(): LiveData<Long> = elapsedTimeState

    private val isFavourite = MutableLiveData<Boolean>()
    fun isFavouriteLiveData(): LiveData<Boolean> = isFavourite

    private val isAdded = MutableLiveData(false)
    fun isAddedLiveData(): LiveData<Boolean> = isAdded

    private val getPlaylistsData: MutableLiveData<SearchResult> =
        MutableLiveData(SearchResult.NotFound)

    fun getPlaylistsLiveData(): LiveData<SearchResult> = getPlaylistsData

    init {
        tracksInteractor.prepare(
            track.previewUrl!!,
            object : PlayerInteractor.OnStateChangeListener {
                override fun onChange(state: PlayerState) {
                    playerState.postValue(state)
                }
            })
        onGetAllAddedTracks()
    }

    private fun onGetAllAddedTracks() {
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetTracks().collect { result ->
                if (result != null && result.isNotEmpty()) {
                    for (item in result) {
                        if (item.trackId == track.trackId) {
                            isAdded.postValue(true)
                            break
                        }
                    }
                }
            }
        }
    }

    fun onAddToPlaylistClicked() {
        var getResult: SearchResult
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetPlaylists().collect { result ->
                if (result != null) {
                    if (result.isEmpty()) {
                        getResult = SearchResult.NotFound
                    } else {
                        getResult =
                            SearchResult.PlaylistContent(
                                result.size,
                                result
                            )
                    }
                } else {
                    getResult =
                        SearchResult.NotFound
                }
                getPlaylistsData.postValue(getResult)
            }
        }
        // эта строка нужна, чтобы при нажатии на кнопку "добавить в плейлист",
        // ее состояние оставалось прежним до тех пор, пока не будет выбран
        // конкретный плейлист
        isAdded.postValue(isAdded.value)
    }

    private fun createIdsListFromJson(json: String): MutableList<String> {
        val mutableListTutorialType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, mutableListTutorialType)
    }

    fun onPlaylistClicked(playlist: Playlist, track: Track): Boolean {
        var isSuccess = true

        if (!playlist.trackIds.isNullOrEmpty()) {
            val idsList = createIdsListFromJson(playlist.trackIds)
            for (id in idsList) {
                if (id == track.trackId.toString()) {
                    isSuccess = false
                    isAdded.postValue(isSuccess)
                    return isSuccess
                }
            }
        }

        try {
            viewModelScope.launch {
                playlistEntitiesUseCase.executeUpdatePlaylist(playlist, track).collect()
            }
        } catch (e: Throwable) {
            isSuccess = false
        }

        isAdded.postValue(isSuccess)
        return isSuccess
    }

    fun onFavoriteClicked() {
        track.isFavorite = !track.isFavorite
        viewModelScope.launch {
            favouriteEntitiesUseCase.executeUpdate(track).collect()
        }
        isFavourite.postValue(track.isFavorite)
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun startPlayer() {
        tracksInteractor.play()
        isPlaying = true
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer() // запускаем таймер
    }

    fun pausePlayer() {
        tracksInteractor.pause()
        isPlaying = false
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        tracksInteractor.destroy()
        isPlaying = false
        playerState.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            timerFlow()
                .collect { value ->
                    elapsedTimeState.postValue(value)
                }
            pausePlayer()
            elapsedTimeState.postValue(0L)
        }
    }

    private fun timerFlow(): Flow<Long> = flow {
        while (tracksInteractor.currentPosition() < PREVIEW_TIME) {
            delay(DELAY)
            emit(tracksInteractor.currentPosition().toLong())
        }
    }.flowOn(Dispatchers.Default)

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(tracksInteractor.currentPosition()) ?: "00:00"
    }

    fun onDestroy() {
        releasePlayer()
    }

    companion object {
        private const val DELAY = 300L
        private const val PREVIEW_TIME = 29_900L
    }
}