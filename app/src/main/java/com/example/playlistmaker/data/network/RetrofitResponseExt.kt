package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.search.api.ApiResponse
import retrofit2.Response

fun <T> Response<T>.mapToApiResponse(): ApiResponse<T> {
    val dto = body()
    val errorMessage = "Error during loading data"

    return when {
        !isSuccessful -> ApiResponse.Error(errorBody()?.string() ?: errorMessage)
        dto != null -> ApiResponse.Success(dto)
        else -> ApiResponse.Error(errorMessage)
    }
}