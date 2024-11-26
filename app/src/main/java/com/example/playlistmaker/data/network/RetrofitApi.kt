package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.search.dto.TracksResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TracksResponseDto

    @GET("/lookup?entity=song")
    suspend fun searchById(@Query("id") trackId: Long): TracksResponseDto
}