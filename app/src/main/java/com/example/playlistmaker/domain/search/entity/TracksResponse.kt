package com.example.playlistmaker.domain.search.entity

sealed interface SearchResult {

    data object Empty : SearchResult
    data object Loading : SearchResult
    data object NotFound : SearchResult

    data class Content(
        val resultCount: Int,
        val results: List<Track>
    ) : SearchResult

    data object Error : SearchResult
}


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
