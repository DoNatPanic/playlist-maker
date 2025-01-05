package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.converters.FavouriteDbConverter
import com.example.playlistmaker.data.search.converters.TrackConverter
import com.example.playlistmaker.domain.db.api.FavouriteRepository
import com.example.playlistmaker.domain.search.entity.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class FavouriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favouriteDbConvertor: FavouriteDbConverter,
    private val trackConverter: TrackConverter,
) : FavouriteRepository {

    override fun updateFavourite(track: Track): Flow<Unit> = flow {
        // дата добавления в избранное
        val time = LocalDateTime.now().toString()
        val favouriteEntity = convertToFavouriteEntity(track, time)
        if (track.isFavorite) {
            emit(insertRequest(favouriteEntity))
        } else {
            emit(deleteRequest(favouriteEntity))
        }
    }

    override fun getFavourites(): Flow<List<Track>> = flow {
        var list = getFavouritesRequest()

        // сортируем по времени добавления в список избранного (по убыванию)
        list = list.sortedByDescending { favourite -> LocalDateTime.parse(favourite.addedDateTime) }
        emit(convertToTrackEntity(list))
    }

    override fun getFavouriteById(trackId: Long): Flow<Track?> = flow {
        var trackEntity = getFavouriteByIdRequest(trackId)
        if (trackEntity != null) {
            emit(favouriteDbConvertor.map(trackEntity))
        } else emit(null)
    }

    private suspend fun getFavouritesRequest(): List<FavouriteEntity> {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.favouriteDao().getFavourites()
            } catch (e: Throwable) {
                listOf()
            }
        }
    }

    private suspend fun getFavouriteByIdRequest(trackId: Long): FavouriteEntity? {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.favouriteDao().getFavouriteById(trackId)
            } catch (e: Throwable) {
                null
            }
        }
    }

    private suspend fun insertRequest(favouriteEntity: FavouriteEntity) {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.favouriteDao().insertFavourite(favouriteEntity)
            } catch (e: Throwable) {
                // nothing
            }
        }
    }

    private suspend fun deleteRequest(favouriteEntity: FavouriteEntity) {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.favouriteDao().deleteFavourite(favouriteEntity)
            } catch (e: Throwable) {
                // nothing
            }
        }
    }

    private fun convertToFavouriteEntity(track: Track, time: String): FavouriteEntity {
        val trackDto = trackConverter.map(track)
        return favouriteDbConvertor.map(trackDto, time)
    }

    private fun convertToTrackEntity(favourites: List<FavouriteEntity>): List<Track> {
        return favourites.map { track -> favouriteDbConvertor.map(track) }
    }
}