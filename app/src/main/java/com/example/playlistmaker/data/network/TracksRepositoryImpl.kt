package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl : TracksRepository {

    override fun getTracks(query: String): Flow<ApiResponse<TracksResponse>> = flow {

        when (val response = RetrofitClient.doRequest(query)) {
            null -> {
                emit(ApiResponse.Error())
            }

            else -> {
                emit(ApiResponse.Success(response))

            }
        }
    }

    override fun getTrackById(trackId: Long): Flow<ApiResponse<TracksResponse>> = flow {
        when (val response = RetrofitClient.doRequestById(trackId)) {
            null -> {
                emit(ApiResponse.Error())
            }

            else -> {
                emit(ApiResponse.Success(response))

            }
        }
    }
}