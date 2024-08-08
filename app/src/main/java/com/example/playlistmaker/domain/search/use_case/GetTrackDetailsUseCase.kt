package com.example.playlistmaker.domain.search.use_case

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksApi
import com.example.playlistmaker.domain.search.consumer.Consumer
import com.example.playlistmaker.domain.search.consumer.ConsumerData
import com.example.playlistmaker.domain.search.entity.TracksResponse
import java.util.concurrent.Executors

class GetTrackDetailsUseCase(
    private val tracksApi: TracksApi
) {
    private val executor = Executors.newCachedThreadPool()
    fun execute(
        trackId: Long?,
        consumer: Consumer<TracksResponse>
    ) {
        executor.execute {
            val track = trackId?.let { tracksApi.getTrackById(trackId) }

            when (track) {
                null -> consumer.consume(ConsumerData.Error())
                is ApiResponse.Error -> consumer.consume(ConsumerData.Error())
                is ApiResponse.Success -> consumer.consume(ConsumerData.Data(track.data))
            }
        }
    }
}
