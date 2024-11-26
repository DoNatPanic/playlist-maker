package com.example.playlistmaker.domain.search.use_case

import com.example.playlistmaker.domain.search.api.ApiResponse
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.entity.TracksResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetTrackDetailsUseCase(
    private val trackId: Long?,
    private val tracksRepository: TracksRepository
) {
    fun execute(): Flow<TracksResponse?> {

        return tracksRepository.getTrackById(trackId!!).map { track ->
            when (track) {
                is ApiResponse.Success -> {
                    track.data
                }

                is ApiResponse.Error -> {
                    null
                }
            }

        }.catch { null }
    }
}
