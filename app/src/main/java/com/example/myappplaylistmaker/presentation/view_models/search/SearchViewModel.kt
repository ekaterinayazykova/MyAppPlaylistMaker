package com.example.myappplaylistmaker.presentation.view_models.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.consumer.ConsumerData
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase

class SearchViewModel (
    private val searchHistoryManager: SearchHistoryManagerInteractor,
    private val searchTrackUseCase: SearchTrackUseCase
) : ViewModel()  {

    private var lastQuery: String? = null

    private val historyTrack: MutableLiveData<List<Track>?> = MutableLiveData()
    val _historyTrack: LiveData<List<Track>?> get() = historyTrack

    private val searchState = MutableLiveData<State>()
    val _searchState: LiveData<State> get() = searchState

    fun getDataFromPref(): List<Track> {
        val result = searchHistoryManager.getSearchHistory()
        if (result.isEmpty()) {
            searchState.value = State.EmptyHistory
        } else {
            searchState.value = State.LoadedHistory
        }
        historyTrack.value = result
        return result
    }

    fun getDataFromServer(query: String) {
        if (query.isEmpty() or query.isBlank()) {
            return
        }
        searchState.postValue(State.Loading)
        searchTrackUseCase.execute(query, object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                when (data) {
                    is ConsumerData.Data -> {
                        val tracks = data.value
                        if (tracks.isNotEmpty()) {
                            searchState.postValue(State.SuccessSearch(tracks))
                        } else {
                            searchState.postValue(State.EmptyMainSearch)
                        }
                    }
                    is ConsumerData.Error -> {
                        searchState.postValue(State.Error)
                        lastQuery = query
                    }
                }
            }
        })
    }

    fun clearedList() {
        searchHistoryManager.clearHistory()
        searchState.value = State.ClearSearchNoQuery
    }

    fun saveToHistory(track: Track) {
        searchHistoryManager.saveToHistory(track)
    }

    sealed class State {
        data object EmptyHistory : State()
        data object LoadedHistory : State()
        data object Error : State()
        data object Loading : State()
        data object EmptyMainSearch : State()
        data object ClearSearch: State()
        data object ClearEditText: State()
        data object ClearSearchNoQuery: State()
        data class SuccessSearch(val listTracks: List<Track>) : State()
    }

}


