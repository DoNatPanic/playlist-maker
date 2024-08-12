package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.shared_prefs.SharedPrefs
import com.example.playlistmaker.data.shared_prefs.TRACKS_HISTORY
import com.example.playlistmaker.domain.search.api.SearchRepository
import com.example.playlistmaker.domain.search.entity.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchRepositoryImpl(
    private val sharedPrefs: SharedPrefs,
    private val gson: Gson,
) : SearchRepository {

    override fun saveHistory(tracks: List<Track>) {
        sharedPrefs.getTracksHistorySP().edit()
            .putString(TRACKS_HISTORY, createJsonFromFact(tracks))
            .apply()
    }

    override fun loadHistory(): MutableList<Track> {
        var list: MutableList<Track> = mutableListOf()
        val trackListString = sharedPrefs.getTracksHistorySP().getString(TRACKS_HISTORY, null)
        if (trackListString != null) {
            list.addAll(createFactFromJson(trackListString))
        }
        return list
    }

    private fun createJsonFromFact(track: List<Track>): String {
        return gson.toJson(track)
    }

    private fun createFactFromJson(json: String): MutableList<Track> {
        val mutableListTutorialType = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, mutableListTutorialType)
    }
}