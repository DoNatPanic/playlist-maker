package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.player.entity.PlayerState

class PlayerRepositoryImpl(
    private val player: MediaPlayer
) : PlayerRepository {

    private lateinit var listener: PlayerInteractor.OnStateChangeListener

    override fun prepare(source: String, listener: PlayerInteractor.OnStateChangeListener) {
        this.listener = listener

        player.setDataSource(source)
        player.prepareAsync()
        player.setOnPreparedListener {
            listener.onChange(PlayerState.Prepared())
        }
    }

    override fun play() {
        player.start()
        listener.onChange(PlayerState.Playing())
    }

    override fun pause() {
        player.pause()
        listener.onChange(PlayerState.Paused())
    }

    override fun destroy() {
        player.stop()
        player.release()
    }
}