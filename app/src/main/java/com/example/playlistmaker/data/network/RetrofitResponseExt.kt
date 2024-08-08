package com.example.playlistmaker.data.network

import android.util.Log
import com.example.playlistmaker.domain.search.api.ApiResponse
import retrofit2.Response

private const val TAG = "MapToApiResponse"

fun <T> Response<T>.mapToApiResponse(): ApiResponse<T> {
    val dto = body()

    return when {
        !isSuccessful -> {
            var errorMsg = errorBody()?.string()
            if (errorMsg != null) {
                Log.w(TAG, errorMsg)
            }
            ApiResponse.Error()
        }
        dto != null -> ApiResponse.Success(dto)
        else -> ApiResponse.Error()
    }
}