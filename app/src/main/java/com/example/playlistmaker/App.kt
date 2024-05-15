package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate

const val DARK_MODE = "dark_mode"
var DARK_MODE_VALUE: Boolean = false

class App : Application() {

    private lateinit var listener: OnSharedPreferenceChangeListener
    override fun onCreate() {
        super.onCreate()

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == DARK_MODE) {
                DARK_MODE_VALUE = sharedPreferences.getBoolean(DARK_MODE, false)
            }
        }
        switchTheme(DARK_MODE_VALUE)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun saveShared(darkThemeEnabled: Boolean){
        var sharedPreferences = getSharedPreferences(DARK_MODE, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_MODE, darkThemeEnabled)
            .apply()
    }
}

