package com.example.playlistmaker.data.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.entity.ThemeSettings

const val DARK_MODE = "dark_mode"

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        // FIXME conversion from bool to enum
        val sharedPreferences = context.getSharedPreferences(DARK_MODE, Application.MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean(DARK_MODE, false)

        var theme = ThemeSettings.LIGHT
        if (darkTheme) {
            theme = ThemeSettings.DARK
        }
        setDefaultNightMode(theme)

        return theme
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        val darkThemeEnabled = settings == ThemeSettings.DARK
        val sharedPreferences = context.getSharedPreferences(DARK_MODE, Application.MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_MODE, darkThemeEnabled)
            .apply()
        setDefaultNightMode(settings)
    }

    private fun setDefaultNightMode(settings: ThemeSettings) {
        val darkThemeEnabled = settings == ThemeSettings.DARK
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}