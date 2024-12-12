package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.use_case.FavouriteEntitiesUseCase
import com.example.playlistmaker.domain.search.entity.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.presentation.utils.SingleEventLiveData
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouriteEntitiesUseCase: FavouriteEntitiesUseCase
) : ViewModel() {

    private val getResultData: MutableLiveData<SearchResult> =
        MutableLiveData(SearchResult.NotFound)

    fun getResultLiveData(): LiveData<SearchResult> = getResultData

    private val openMediaPlayerTrigger = SingleEventLiveData<Track>()
    fun getOpenMediaPlayerTrigger(): LiveData<Track> = openMediaPlayerTrigger

    fun onReload() {
        var getResult: SearchResult = SearchResult.NotFound
        getResultData.postValue(getResult)
        viewModelScope.launch {
            favouriteEntitiesUseCase.executeGet().collect { result ->
                if (result != null) {
                    if (result.isEmpty()) {
                        getResult = SearchResult.NotFound
                    } else {
                        getResult =
                            SearchResult.Content(
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

    fun onTrackClicked(track: Track) {
        openMediaPlayerTrigger.value = track
    }
}