package com.example.playlistmaker.domain.search.use_case

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTrackListUseCase(
    private val tracksRepository: TracksRepository
) {

    fun execute(query: String): Flow<TracksResponse?> {
        return tracksRepository.getTracks(query).map { result ->
            when (result) {
                is ApiResponse.Success -> {
                    result.data
                }

                is ApiResponse.Error -> {
                    null
                }
            }
        }
    }
}