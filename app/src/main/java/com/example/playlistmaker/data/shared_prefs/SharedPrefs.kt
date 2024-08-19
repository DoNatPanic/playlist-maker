package com.example.playlistmaker.data.shared_prefs

import android.content.SharedPreferences

interface SharedPrefs {
    fun getTracksHistorySP(): SharedPreferences

    fun getThemeSettingsSP(): SharedPreferences
}