package com.example.playlistmaker.domain.db.use_case

import com.example.playlistmaker.domain.db.api.FavouriteRepository
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow

class FavouriteEntitiesUseCase(
    private val favouriteRepository: FavouriteRepository
) {

    fun executeUpdate(track: Track): Flow<Unit> {
        return favouriteRepository.updateFavourite(track)
    }

    fun executeGet(): Flow<List<Track>> {
        return favouriteRepository.getFavourites()
    }

    fun executeGetById(trackId: Long): Flow<Track?> {
        return favouriteRepository.getFavouriteById(trackId)
    }
}