package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun readHistory(): MutableList<Track> {
        var list: MutableList<Track> = mutableListOf()
        val trackListString = sharedPreferences.getString(TRACKS_HISTORY, null)
        if (trackListString != null) {
            list.addAll(createFactFromJson(trackListString))
        }
        return list
    }

    fun saveToShared(list: MutableList<Track>) {
        sharedPreferences.edit()
            .putString(TRACKS_HISTORY, createJsonFromFact(list))
            .apply()
    }

    private fun createJsonFromFact(track: MutableList<Track>): String {
        return Gson().toJson(track)
    }

    private fun createFactFromJson(json: String): MutableList<Track> {
        val mutableListTutorialType = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, mutableListTutorialType)
    }
}