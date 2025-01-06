package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.db.use_case.PlaylistEntitiesUseCase
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistEntitiesUseCase: PlaylistEntitiesUseCase
) : ViewModel() {

    private val getResultData: MutableLiveData<SearchResult> =
        MutableLiveData(SearchResult.NotFound)

    fun getResultLiveData(): LiveData<SearchResult> = getResultData
    fun onReload() {
        var getResult: SearchResult = SearchResult.NotFound
        getResultData.postValue(getResult)
        viewModelScope.launch {
            playlistEntitiesUseCase.executeGetPlaylists().collect { result ->
                if (result != null) {
                    if (result.isEmpty()) {
                        getResult = SearchResult.NotFound
                    } else {
                        getResult =
                            SearchResult.PlaylistContent(
                                result.size,
                                result
                            )
                    }
                } else {
                    getResult =
                        SearchResult.NotFound
                }
                getResultData.postValue(getResult)
            }
        }
    }
}