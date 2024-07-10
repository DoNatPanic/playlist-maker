package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {
    override fun prepare(source: String?, listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.prepare(source, listener)
    }

    override fun play() {
        playerRepository.play()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun destroy() {
        playerRepository.destroy()
    }
}