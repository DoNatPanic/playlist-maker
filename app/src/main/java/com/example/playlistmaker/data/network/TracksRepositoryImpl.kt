package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse

class TracksRepositoryImpl : TracksRepository {

    override fun getTracks(query: String): ApiResponse<TracksResponse> {
        return RetrofitClient.api.search(query).execute().mapToApiResponse()
    }

    override fun getTrackById(trackId: Long): ApiResponse<TracksResponse> {
        return RetrofitClient.api.searchById(trackId).execute().mapToApiResponse()
    }
}