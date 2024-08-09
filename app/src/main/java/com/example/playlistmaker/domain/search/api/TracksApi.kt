package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.entity.TracksResponse

interface TracksApi {
    fun getTracks(query: String): ApiResponse<TracksResponse>
    fun getTrackById(trackId: Long): ApiResponse<TracksResponse>
}