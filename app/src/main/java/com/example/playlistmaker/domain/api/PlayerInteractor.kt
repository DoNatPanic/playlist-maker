package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.entity.PlayerState

interface PlayerInteractor {
    fun prepare(source: String?, listener: OnStateChangeListener)
    fun play()
    fun pause()
    fun destroy()
    interface OnStateChangeListener {
        fun onChange(state: PlayerState)
    }
}