package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.search.converters.TrackConverter
import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val trackConverter: TrackConverter,
    private val appDatabase: AppDatabase,
) : TracksRepository {

    override fun getTracks(query: String): Flow<ApiResponse<TracksResponse>> = flow {

        when (val response = RetrofitClient.doRequest(query)) {
            null -> {
                emit(ApiResponse.Error())
            }

            else -> {
                val data = trackConverter.convert(response)

                // get favourite tracks id's from database
                val favouritesIds = doRequest()

                // compare tracks from iTunes with favourite tracks from DB
                for (track in data.results) {
                    for (id in favouritesIds) {
                        if (track.trackId == id) {
                            track.isFavorite = true
                        }
                    }
                }
                emit(ApiResponse.Success(data = data))
            }
        }
    }

    private suspend fun doRequest(): List<Long> {
        return withContext(Dispatchers.IO) {
            try {
                appDatabase.favouriteDao().getFavouritesIds()
            } catch (e: Throwable) {
                listOf()
            }
        }
    }
}