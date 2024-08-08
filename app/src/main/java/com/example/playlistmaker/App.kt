package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)
        val theme = settingsInteractor.getThemeSettings()

        settingsInteractor.updateThemeSetting(theme)
    }
}

