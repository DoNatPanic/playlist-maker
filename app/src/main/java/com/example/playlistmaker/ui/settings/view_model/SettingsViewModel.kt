package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.entity.ThemeSettings
import com.example.playlistmaker.domain.sharing.api.SharingInteractor

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

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

    companion object {
        fun getSettingsViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(
                        this[APPLICATION_KEY] as Application,
                        Creator.provideSharingInteractor(this[APPLICATION_KEY] as Application),
                        Creator.provideSettingsInteractor(this[APPLICATION_KEY] as Application),
                    )
                }
            }
    }
}