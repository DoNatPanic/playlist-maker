package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ContactProviderImpl
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.api.SearchRepository
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.api.ContactProvider
import com.example.playlistmaker.domain.sharing.api.ExternalNavigator
import com.example.playlistmaker.domain.sharing.api.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    // audio player
    single {
        MediaPlayer()
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    // search
    factory<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    // theme
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    // sharing
    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    // contact
    factory<ContactProvider> {
        ContactProviderImpl(androidContext())
    }
}