package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.use_case.PlaylistEntitiesUseCase
import com.example.playlistmaker.domain.media.entity.Playlist
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistEntitiesUseCase: PlaylistEntitiesUseCase
) : ViewModel() {

    fun onCreatePlaylistClicked(playlist: Playlist) {
        viewModelScope.launch {
            playlistEntitiesUseCase.executeSave(playlist).collect()
        }
    }
}