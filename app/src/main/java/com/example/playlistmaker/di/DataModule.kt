package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.search.converters.TrackConverter
import com.example.playlistmaker.data.shared_prefs.SharedPrefs
import com.example.playlistmaker.data.shared_prefs.SharedPrefsImpl
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.use_case.GetTrackListUseCase
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<SharedPrefs> {
        SharedPrefsImpl(androidContext())
    }

    factory { Gson() }

    factory { TrackConverter() }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<GetTrackListUseCase> {
        GetTrackListUseCase(get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}