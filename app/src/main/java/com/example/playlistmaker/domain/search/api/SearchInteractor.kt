package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.entity.Track

interface SearchInteractor {
    fun loadHistory(): MutableList<Track>
    fun saveHistory(tracks: List<Track>)
}