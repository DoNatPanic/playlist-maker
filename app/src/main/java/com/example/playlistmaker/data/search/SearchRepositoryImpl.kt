package com.example.playlistmaker.data.search

import android.content.Context
import com.example.playlistmaker.domain.search.api.SearchRepository
import com.example.playlistmaker.domain.search.entity.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACKS_HISTORY = "tracks_history"

class SearchRepositoryImpl(
    private val context: Context,
) : SearchRepository {

    private val sharedPreferences =
        context.getSharedPreferences(TRACKS_HISTORY, Context.MODE_PRIVATE)

    override fun saveHistory(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(TRACKS_HISTORY, createJsonFromFact(tracks))
            .apply()
    }

    override fun loadHistory(): MutableList<Track> {
        var list: MutableList<Track> = mutableListOf()
        val trackListString = sharedPreferences.getString(TRACKS_HISTORY, null)
        if (trackListString != null) {
            list.addAll(createFactFromJson(trackListString))
        }
        return list
    }

    private fun createJsonFromFact(track: List<Track>): String {
        return Gson().toJson(track)
    }

    private fun createFactFromJson(json: String): MutableList<Track> {
        val mutableListTutorialType = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, mutableListTutorialType)
    }
}