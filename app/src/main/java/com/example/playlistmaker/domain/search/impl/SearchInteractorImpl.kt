package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.api.SearchRepository
import com.example.playlistmaker.domain.search.entity.Track

class SearchInteractorImpl(
    private val searchRepository: SearchRepository
) : SearchInteractor {

    override fun loadHistory(): MutableList<Track> {
        return searchRepository.loadHistory()
    }

    override fun saveHistory(tracks: List<Track>) {
        searchRepository.saveHistory(tracks)
    }

}