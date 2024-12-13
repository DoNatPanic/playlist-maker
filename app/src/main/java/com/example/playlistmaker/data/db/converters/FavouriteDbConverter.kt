package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.FavouriteEntity
import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.domain.search.entity.Track

class FavouriteDbConverter {

    fun map(track: TrackDto, time: String): FavouriteEntity {
        return FavouriteEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            addedDateTime = time
        )
    }

    fun map(entity: FavouriteEntity): Track {
        return Track(
            entity.trackId,
            entity.trackName,
            entity.artistName,
            entity.trackTime,
            entity.artworkUrl100,
            entity.collectionName,
            entity.releaseDate,
            entity.primaryGenreName,
            entity.country,
            entity.previewUrl,
            isFavorite = true
        )
    }
}