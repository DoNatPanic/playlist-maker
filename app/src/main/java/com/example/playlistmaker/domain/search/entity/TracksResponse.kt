package com.example.playlistmaker.domain.search.entity

sealed interface TrackSearchHistory {
    data object Empty : TrackSearchHistory

    data class Content(
        val resultCount: Int,
        val results: List<Track>
    ) : TrackSearchHistory
}

class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)
