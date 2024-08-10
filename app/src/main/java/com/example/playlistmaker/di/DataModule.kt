package com.example.playlistmaker.di

import com.example.playlistmaker.data.network.RetrofitTracksApi
import com.example.playlistmaker.domain.search.api.TracksApi
import com.example.playlistmaker.domain.search.use_case.GetTrackDetailsUseCase
import com.example.playlistmaker.domain.search.use_case.GetTrackListUseCase
import org.koin.dsl.module

val dataModule = module {

    single<TracksApi> {
        RetrofitTracksApi()
    }

    factory<GetTrackListUseCase> { (query: String) ->
        GetTrackListUseCase(query, get())
    }

    factory<GetTrackDetailsUseCase> {
        GetTrackDetailsUseCase(get())
    }
}