package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.FavouriteInteractor
import com.example.playlistmaker.domain.db.api.FavouriteRepository
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteInteractorImpl(
    private val favouriteRepository: FavouriteRepository
) : FavouriteInteractor {

    override fun updateFavourite(track: Track): Flow<Unit> = flow {
        favouriteRepository.updateFavourite(track)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return favouriteRepository.getFavourites()
    }
}