package com.example.playlistmaker.domain.player.entity

sealed class PlayerState(val isPlayButtonChecked: Boolean) {

    class Default : PlayerState(false )

    class Prepared : PlayerState(false)

    class Playing : PlayerState(true)

    class Paused : PlayerState(false)
}