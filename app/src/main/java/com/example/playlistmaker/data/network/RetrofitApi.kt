package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.search.entity.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TracksResponse>

    @GET("/lookup?entity=song")
    fun searchById(@Query("id") trackId: Long): Call<TracksResponse>
}