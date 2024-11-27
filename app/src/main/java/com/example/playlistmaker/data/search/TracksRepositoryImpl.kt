package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.search.converters.TrackConverter
import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val trackConverter: TrackConverter,
) : TracksRepository {

    override fun getTracks(query: String): Flow<ApiResponse<TracksResponse>> = flow {

        when (val response = RetrofitClient.doRequest(query)) {
            null -> {
                emit(ApiResponse.Error())
            }

            else -> {
                emit(ApiResponse.Success(data = trackConverter.convert(response)))

            }
        }
    }
}