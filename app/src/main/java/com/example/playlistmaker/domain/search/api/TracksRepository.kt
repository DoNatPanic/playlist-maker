package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun getTracks(query: String): Flow<ApiResponse<TracksResponse>>
    fun getTrackById(trackId: Long): Flow<ApiResponse<TracksResponse>>
}