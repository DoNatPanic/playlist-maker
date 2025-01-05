package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.db.use_case.FavouriteEntitiesUseCase
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TrackSearchHistory
import com.example.playlistmaker.domain.search.use_case.GetTrackListUseCase
import com.example.playlistmaker.presentation.utils.SingleEventLiveData
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SearchViewModel(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val favouriteEntitiesUseCase: FavouriteEntitiesUseCase,
    private val searchInteractor: SearchInteractor
) : ViewModel(), KoinComponent {

    private var trackHistoryList: MutableList<Track> = mutableListOf()
    private val searchResultData: MutableLiveData<SearchResult> =
        MutableLiveData(SearchResult.Empty)

    fun searchResultLiveData(): LiveData<SearchResult> = searchResultData

    private var searchHistoryData: SingleEventLiveData<TrackSearchHistory> = SingleEventLiveData()
    fun searchHistoryLiveData(): LiveData<TrackSearchHistory> = searchHistoryData

    private val openMediaPlayerTrigger = SingleEventLiveData<Track>()
    fun getOpenMediaPlayerTrigger(): SingleEventLiveData<Track> = openMediaPlayerTrigger

    private fun trackHistoryListUpdate() {
        trackHistoryList = searchInteractor.loadHistory()
        if (trackHistoryList.size != 0) {
            searchHistoryData.postValue(
                TrackSearchHistory.Content(
                    trackHistoryList.size,
                    trackHistoryList
                )
            )
        } else {
            searchHistoryData.postValue(TrackSearchHistory.Empty)
        }
    }

    init {
        trackHistoryListUpdate()
    }

    fun onReload() {
        trackHistoryListUpdate()
    }

    fun clearSearchResults() {
        searchResultData.postValue(SearchResult.Empty)
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) {
            return
        }

        var searchResult: SearchResult = SearchResult.Loading
        searchResultData.postValue(searchResult)

        viewModelScope.launch {
            getTrackListUseCase
                .execute(query)
                .collect { result ->
                    if (result != null) {
                        if (result.resultCount == 0) {
                            searchResult = SearchResult.NotFound
                        } else {
                            searchResult =
                                SearchResult.TrackContent(
                                    result.resultCount,
                                    result.results
                                )

                        }
                    } else {
                        searchResult =
                            SearchResult.Error
                    }
                    searchResultData.postValue(searchResult)
                }
        }
    }

    fun onClearHistory() {
        trackHistoryList.clear()
        searchHistoryData.postValue(TrackSearchHistory.Empty)
        searchInteractor.saveHistory(trackHistoryList)
    }

    // отображается история и пользователь нажал на трек истории
    fun onHistoryTrackClicked(track: Track) {
        if (trackHistoryList.size > 1) {
            // перестроили список
            trackHistoryList.find { elem -> elem.trackId == track.trackId }?.let {
                trackHistoryList.remove(it)
                trackHistoryList.add(0, it)
                // сохранили его в SP
                searchInteractor.saveHistory(trackHistoryList)
                searchHistoryData.postValue(
                    TrackSearchHistory.Content(
                        trackHistoryList.size,
                        trackHistoryList
                    )
                )
            }
        }
        compareTrackWithFavouritesTable(track)
    }

    // пользователь выбрал песню из списка
    fun onSearchTrackClicked(track: Track) {
        if (trackHistoryList.size > 0) {
            var isExist = false
            var existElement: Track? = null
            trackHistoryList.forEach { element ->
                // выбранный трек уже есть в истории - замоминаем его
                if (element.trackId == track.trackId) {
                    isExist = true
                    existElement = element
                }
            }
            // удаляем его из  списка истории
            if (isExist) {
                trackHistoryList.remove(existElement)
            }
            if (trackHistoryList.size >= 10) {
                trackHistoryList.removeLast()
            }
        }
        // и добавляем его в начало списка
        trackHistoryList.add(0, track)
        searchInteractor.saveHistory(trackHistoryList)
        searchHistoryData.postValue(
            TrackSearchHistory.Content(
                trackHistoryList.size,
                trackHistoryList
            )
        )
        openMediaPlayerTrigger.value = track
    }


    private fun compareTrackWithFavouritesTable(track: Track) {
        viewModelScope.launch {
            favouriteEntitiesUseCase.executeGetById(track.trackId).collect { result ->
                if (result != null) {
                    openMediaPlayerTrigger.postValue(result!!)
                } else {
                    openMediaPlayerTrigger.postValue(track)
                }
            }
        }
    }

}