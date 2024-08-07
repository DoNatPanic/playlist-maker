package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.network.RetrofitTracksApi
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.api.SearchRepository
import com.example.playlistmaker.domain.search.api.TracksApi
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.search.use_case.GetTrackDetailsUseCase
import com.example.playlistmaker.domain.search.use_case.GetTrackListUseCase
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.api.ExternalNavigator
import com.example.playlistmaker.domain.sharing.api.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator : Application() {
    private fun getAudioPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getAudioPlayerRepository())
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSharingRepository(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(context, getSharingRepository(context))
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(context)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
    }

    private fun provideTracksApi(): TracksApi {
        return RetrofitTracksApi()
    }
    fun provideGetTracksUseCase(query: String): GetTrackListUseCase {
        return GetTrackListUseCase(query, provideTracksApi())
    }

    fun provideGetTrackDetailsUseCase(): GetTrackDetailsUseCase {
        return GetTrackDetailsUseCase(provideTracksApi())
    }
}