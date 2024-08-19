package com.example.playlistmaker.data.settings

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.shared_prefs.DARK_MODE
import com.example.playlistmaker.data.shared_prefs.SharedPrefs
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.entity.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPrefs
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        // FIXME conversion from bool to enum
        val darkTheme = sharedPrefs.getThemeSettingsSP().getBoolean(DARK_MODE, false)

        var theme = ThemeSettings.LIGHT
        if (darkTheme) {
            theme = ThemeSettings.DARK
        }
        setDefaultNightMode(theme)

        return theme
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        val darkThemeEnabled = settings == ThemeSettings.DARK
        sharedPrefs.getThemeSettingsSP().edit()
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