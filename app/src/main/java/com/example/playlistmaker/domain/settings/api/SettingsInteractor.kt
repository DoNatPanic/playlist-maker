package com.example.playlistmaker.domain.settings.api

import com.example.playlistmaker.domain.settings.entity.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}