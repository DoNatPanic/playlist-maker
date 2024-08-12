package com.example.playlistmaker.data.shared_prefs

import android.content.Context
import android.content.SharedPreferences

const val TRACKS_HISTORY = "tracks_history"
const val DARK_MODE = "dark_mode"

class SharedPrefsImpl(
    private val context: Context

) : SharedPrefs {
    override fun getTracksHistorySP(): SharedPreferences {
        return context
            .getSharedPreferences(DARK_MODE, Context.MODE_PRIVATE)
    }

    override fun getThemeSettingsSP(): SharedPreferences {
        return context
            .getSharedPreferences(TRACKS_HISTORY, Context.MODE_PRIVATE)
    }

}