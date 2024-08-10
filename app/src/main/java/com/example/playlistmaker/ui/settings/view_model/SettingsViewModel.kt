package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.entity.ThemeSettings
import com.example.playlistmaker.domain.sharing.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val darkTheme = MutableLiveData<ThemeSettings>()
    fun darkThemeLiveData(): LiveData<ThemeSettings> = darkTheme

    init {
        val theme = settingsInteractor.getThemeSettings()
        darkTheme.postValue(theme)
    }

    fun onDarkThemeSwitched(enableDarkTheme: Boolean) {
        var theme = ThemeSettings.LIGHT
        if (enableDarkTheme) {
            theme = ThemeSettings.DARK
        }
        settingsInteractor.updateThemeSetting(theme)
        darkTheme.postValue(theme)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}