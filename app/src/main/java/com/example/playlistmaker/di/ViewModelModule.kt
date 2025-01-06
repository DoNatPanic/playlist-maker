package com.example.playlistmaker.di

import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerViewModel
import com.example.playlistmaker.ui.media.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.ui.media.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavouritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        CreatePlaylistViewModel(get())
    }
    viewModel {
        PlaylistInfoViewModel(get(), get())
    }
}