package com.example.playlistmaker.domain.search.use_case

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.consumer.Consumer
import com.example.playlistmaker.domain.search.consumer.ConsumerData
import com.example.playlistmaker.domain.search.entity.TracksResponse
import java.util.concurrent.Executors

class GetTrackListUseCase(
    private val query: String,
    private val tracksRepository: TracksRepository
) {
    private val executor = Executors.newCachedThreadPool()

    fun execute(
        consumer: Consumer<TracksResponse>
    ) {
        executor.execute {
            when (val tracks = tracksRepository.getTracks(query)) {
                is ApiResponse.Error -> consumer.consume(ConsumerData.Error())
                is ApiResponse.Success -> consumer.consume(ConsumerData.Data(tracks.data))
            }
        }
    }
}