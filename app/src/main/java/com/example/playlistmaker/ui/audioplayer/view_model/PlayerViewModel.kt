package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.use_case.FavouriteEntitiesUseCase
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.entity.Track
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
    private val favouriteEntitiesUseCase: FavouriteEntitiesUseCase
) : ViewModel(), KoinComponent {

    private var timerJob: Job? = null

    private var isPlaying: Boolean = false

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val elapsedTimeState = MutableLiveData<Long>(0L)
    fun elapsedTimeLiveData(): LiveData<Long> = elapsedTimeState

    private val isFavourite = MutableLiveData<Boolean>()
    fun isFavouriteLiveData(): LiveData<Boolean> = isFavourite

    init {
        tracksInteractor.prepare(
            track.previewUrl!!,
            object : PlayerInteractor.OnStateChangeListener {
                override fun onChange(state: PlayerState) {
                    playerState.postValue(state)
                }
            })
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