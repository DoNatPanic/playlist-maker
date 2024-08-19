package com.example.playlistmaker.ui.audioplayer.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.consumer.Consumer
import com.example.playlistmaker.domain.search.consumer.ConsumerData
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TracksResponse
import com.example.playlistmaker.domain.search.use_case.GetTrackDetailsUseCase
import org.koin.core.component.KoinComponent

class PlayerViewModel(
    trackId: Long,
    private val tracksInteractor: PlayerInteractor,

    ) : ViewModel(), KoinComponent {

    private var mainThreadHandler: Handler? = null

    private val playerState = MutableLiveData(PlayerState.DEFAULT)
    fun playerStateLiveData(): LiveData<PlayerState> = playerState

    private val trackState: MutableLiveData<Track?> = MutableLiveData()
    fun trackLiveData(): LiveData<Track?> = trackState

    private val elapsedTimeState = MutableLiveData(0L)
    fun elapsedTimeLiveData(): LiveData<Long> = elapsedTimeState

    init {
        val getTrackDetailsUseCase: GetTrackDetailsUseCase = getKoin().get()
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
    }
}