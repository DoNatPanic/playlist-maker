package com.example.playlistmaker.domain.common

import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track

sealed interface SearchResult {

    data object Empty : SearchResult
    data object Loading : SearchResult
    data object NotFound : SearchResult

    data class TrackContent(
        val resultCount: Int,
        val results: List<Track>
    ) : SearchResult

    data class PlaylistContent(
        val resultCount: Int,
        val results: List<Playlist>
    ) : SearchResult

    data object Error : SearchResult
}