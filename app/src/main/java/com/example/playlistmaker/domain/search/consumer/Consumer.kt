package com.example.playlistmaker.domain.search.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}
