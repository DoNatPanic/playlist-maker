package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun prepare(source: String?, listener: PlayerInteractor.OnStateChangeListener)
    fun play()
    fun pause()
    fun destroy()
}
