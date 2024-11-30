package com.example.playlistmaker.domain.player.entity

sealed class PlayerState(val isPlayButtonChecked: Boolean, val progress: String) {

    class Default : PlayerState(false, "00:00")

    class Prepared : PlayerState(false, "00:00")

    class Playing(progress: String) : PlayerState(true, progress)

    class Paused(progress: String) : PlayerState(false, progress)
}