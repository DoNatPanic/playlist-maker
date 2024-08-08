package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.entity.Track

interface SearchRepository {
    fun loadHistory(): MutableList<Track>
    fun saveHistory(tracks: List<Track>)
}