package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PlayerViewModel(
    track: Track,
    private val tracksInteractor: PlayerInteractor,

    ) : ViewModel(), KoinComponent {

    private var timerJob: Job? = null

    private var isPlaying: Boolean = false

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val elapsedTimeState = MutableLiveData<Long>(0L)
    fun elapsedTimeLiveData(): LiveData<Long> = elapsedTimeState

    init {
        tracksInteractor.prepare(
            track.previewUrl!!,
            object : PlayerInteractor.OnStateChangeListener {
                override fun onChange(state: PlayerState) {
                    playerState.postValue(state)
                }
            })
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
        playerState.postValue(PlayerState.Playing())
        startTimer() // запускаем таймер
    }

    fun pausePlayer() {
        tracksInteractor.pause()
        isPlaying = false
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused())
    }

    private fun releasePlayer() {
        tracksInteractor.destroy()
        isPlaying = false
        playerState.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            timerFlow(elapsedTimeState.value!!)
                .collect { value ->
                    elapsedTimeState.postValue(value)
                }
            pausePlayer()
            elapsedTimeState.postValue(0L)
        }
    }

    private fun timerFlow(elTime: Long): Flow<Long> = flow {
        (elTime until PREVIEW_TIME step DELAY).forEach {
            delay(DELAY)
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    fun onDestroy() {
        releasePlayer()
    }

    companion object {
        private const val DELAY = 300L
        private const val PREVIEW_TIME = 30_000L
    }
}