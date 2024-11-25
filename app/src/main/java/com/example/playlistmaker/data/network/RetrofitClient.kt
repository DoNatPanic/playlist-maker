package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: RetrofitApi by lazy {
        client.create(RetrofitApi::class.java)
    }

    suspend fun doRequest(dto: String): TracksResponse? {

        return withContext(Dispatchers.IO) {
            try {
                api.search(dto)
            } catch (e: Throwable) {
                null
            }
        }
    }

    suspend fun doRequestById(dto: Long): TracksResponse? {

        return withContext(Dispatchers.IO) {
            try {
                api.searchById(dto)
            } catch (e: Throwable) {
                null
            }
        }
    }
}
