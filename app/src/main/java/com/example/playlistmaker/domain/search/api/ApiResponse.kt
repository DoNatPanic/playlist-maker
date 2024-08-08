package com.example.playlistmaker.domain.search.api

sealed interface ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>
    class Error<T> : ApiResponse<T>
}