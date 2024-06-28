package com.example.playlistmaker.creator

import android.media.MediaPlayer
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl

object Creator {
    private fun getAudioPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getAudioPlayerRepository())
    }
}