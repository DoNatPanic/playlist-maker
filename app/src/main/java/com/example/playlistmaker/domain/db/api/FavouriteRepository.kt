package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun updateFavourite(track: Track): Flow<Unit>
    fun getFavourites(): Flow<List<Track>>
    fun getFavouriteById(trackId: Long): Flow<Track?>
}