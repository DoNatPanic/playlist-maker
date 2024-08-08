package com.example.playlistmaker.domain.settings.api

import com.example.playlistmaker.domain.settings.entity.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}