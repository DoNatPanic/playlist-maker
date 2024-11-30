package com.example.playlistmaker.domain.player.api

interface PlayerRepository {
    fun prepare(source: String, listener: PlayerInteractor.OnStateChangeListener)
    fun play()
    fun pause()
    fun destroy()
    fun currentPosition(): Int
}
