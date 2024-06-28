package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.entity.PlayerState

class PlayerRepositoryImpl() : PlayerRepository {

    private val player = MediaPlayer()
    private lateinit var listener: PlayerInteractor.OnStateChangeListener

    override fun prepare(source: String?, listener: PlayerInteractor.OnStateChangeListener) {
        this.listener = listener

        player.setDataSource(source)
        player.prepareAsync()
        player.setOnPreparedListener {
            listener.onChange(PlayerState.PREPARED)
        }
    }

    override fun play() {
        player.start()
        listener.onChange(PlayerState.PLAYING)

    }

    override fun pause() {
        player.pause()
        listener.onChange(PlayerState.PAUSED)
    }

    override fun destroy() {
        player.release()
    }
}