package com.example.playlistmaker.domain.search.consumer

sealed interface ConsumerData<T> {
    data class Data<T>(val value: T) : ConsumerData<T>
    class Error<T> : ConsumerData<T>
}