package com.example.playlistmaker.data.search.converters

import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.data.search.dto.TracksResponseDto
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TracksResponse

class TrackConverter {

    fun convert(response: TracksResponseDto): TracksResponse {
        return with(response) {
            TracksResponse(resultCount = this.resultCount,
                results = results.map {
                    it.toTrack()
                })
        }
    }

    private fun TrackDto.toTrack(): Track {
        return Track(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTime,
            artworkUrl100 = this.artworkUrl100,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl
        )
    }
}
