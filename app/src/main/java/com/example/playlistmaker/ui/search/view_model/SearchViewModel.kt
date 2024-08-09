package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.consumer.Consumer
import com.example.playlistmaker.domain.search.consumer.ConsumerData
import com.example.playlistmaker.domain.search.entity.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TrackSearchHistory
import com.example.playlistmaker.domain.search.entity.TracksResponse
import com.example.playlistmaker.presentation.utils.SingleEventLiveData

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor
) : AndroidViewModel(application) {

    private var trackHistoryList: MutableList<Track> = mutableListOf()
    private val searchResultData: MutableLiveData<SearchResult> =
        MutableLiveData(SearchResult.Empty)

    fun searchResultLiveData(): LiveData<SearchResult> = searchResultData

    private var searchHistoryData: SingleEventLiveData<TrackSearchHistory> = SingleEventLiveData()
    fun searchHistoryLiveData(): LiveData<TrackSearchHistory> = searchHistoryData

    init {
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

    private val openMediaPlayerTrigger = SingleEventLiveData<Track>()
    fun getOpenMediaPlayerTrigger(): LiveData<Track> = openMediaPlayerTrigger

    fun clearSearchResults() {
        searchResultData.postValue(SearchResult.Empty)
    }

    fun searchTracks(query: String) {
        val getTrackListUseCase = Creator.provideGetTracksUseCase(query)
        if (query.isEmpty()) {
            return
        }

        var searchResult: SearchResult = SearchResult.Loading
        searchResultData.postValue(searchResult)

        getTrackListUseCase.execute(
            consumer = object : Consumer<TracksResponse> {
                override fun consume(data: ConsumerData<TracksResponse>) {
                    when (data) {
                        is ConsumerData.Error -> {
                            searchResult =
                                SearchResult.Error
                        }

                        is ConsumerData.Data -> {
                            val result = data.value
                            if (result.resultCount == 0) {
                                searchResult = SearchResult.NotFound
                            } else {
                                searchResult =
                                    SearchResult.Content(
                                        data.value.resultCount,
                                        data.value.results
                                    )
                            }
                        }
                    }
                    searchResultData.postValue(searchResult)
                }
            }
        )
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
        openMediaPlayerTrigger.value = track
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

    companion object {
        fun getSearchViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                    SearchViewModel(
                        application,
                        Creator.provideSearchInteractor(application)
                    )
                }
            }
    }
}