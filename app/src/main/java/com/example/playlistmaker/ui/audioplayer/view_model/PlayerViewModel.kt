package com.example.playlistmaker.ui.audioplayer.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.consumer.Consumer
import com.example.playlistmaker.domain.search.consumer.ConsumerData
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TracksResponse

class PlayerViewModel(
    trackId: Long,
    private val tracksInteractor: PlayerInteractor,
) : ViewModel() {

    private var mainThreadHandler: Handler? = null

    private var playerState = MutableLiveData(PlayerState.DEFAULT)
    fun playerStateLiveData(): LiveData<PlayerState> = playerState

    private var trackState: MutableLiveData<Track?> = MutableLiveData()
    fun trackLiveData(): LiveData<Track?> = trackState

    private var elapsedTimeState = MutableLiveData(0L)
    fun elapsedTimeLiveData(): LiveData<Long> = elapsedTimeState

    init {
        val getTrackDetailsUseCase = Creator.provideGetTrackDetailsUseCase()
        getTrackDetailsUseCase.execute(trackId,
            consumer = object : Consumer<TracksResponse> {
                override fun consume(data: ConsumerData<TracksResponse>) {
                    when (data) {
                        is ConsumerData.Data -> {
                            if (data.value.resultCount == 1) {
                                val track = data.value.results[0]
                                trackState.postValue(track)
                                tracksInteractor.prepare(
                                    track.previewUrl,
                                    object : PlayerInteractor.OnStateChangeListener {
                                        override fun onChange(state: PlayerState) {
                                            playerState.postValue(state)
                                        }
                                    })
                            }
                            // else do nothing
                        }

                        is ConsumerData.Error -> {
                            // do nothing
                        }
                    }
                }
            }
        )

        // Создаём Handler, привязанный к ГЛАВНОМУ потоку
        mainThreadHandler = Handler(Looper.getMainLooper())
    }

    private fun startTimer() {
        // Запоминаем время начала таймера
        val startTime = System.currentTimeMillis()

        // И отправляем задачу в Handler
        // Число секунд переводим в миллисекунды
        mainThreadHandler?.post(
            createUpdateTimerTask(startTime, PREVIEW_TIME, elapsedTimeState.value!!)
        )
    }

    private fun createUpdateTimerTask(startTime: Long, duration: Long, elTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                // Сколько прошло времени с момента запуска таймера
                var elapsedTime = System.currentTimeMillis() - startTime + elTime

                if (elapsedTime <= duration) {
                    if (playerState.value == PlayerState.PLAYING) {
                        // Если всё ещё отсчитываем секунды —
                        // обновляем UI и снова планируем задачу
                        elapsedTimeState.postValue(elapsedTime)
                        mainThreadHandler?.postDelayed(this, DELAY)
                    }
                } else {
                    // Если таймер окончен, останавливаем воспроизведение
                    tracksInteractor.pause()
                    elapsedTime = 0L
                    elapsedTimeState.postValue(elapsedTime)
                }
            }
        }
    }

    fun play() {
        if (playerState.value == PlayerState.PREPARED || playerState.value == PlayerState.PAUSED) {
            tracksInteractor.play()
            startTimer() // запускаем таймер
        }
    }

    fun pause() {
        if (playerState.value == PlayerState.PLAYING) {
            tracksInteractor.pause()
        }
    }

    fun onDestroy() {
        tracksInteractor.destroy()
    }

    companion object {
        private const val DELAY = 1000L
        private const val PREVIEW_TIME = 30_000L

        fun getPlayerViewModelFactory(trackId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(
                        trackId,
                        Creator.provideAudioPlayerInteractor(),
                    ) as T
                }
            }
    }
}