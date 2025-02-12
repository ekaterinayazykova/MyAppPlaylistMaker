package com.example.myappplaylistmaker.presentation.view_models.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryManager: SearchHistoryManagerInteractor,
    private val searchTrackUseCase: SearchTrackUseCase,
    private val stringProvider: StringProvider
) : ViewModel() {

    private var lastQuery: String? = null

    private val _historyTrack: MutableLiveData<List<Track>?> = MutableLiveData()
    val historyTrack: LiveData<List<Track>?> get() = this._historyTrack

    private val _searchState = MutableLiveData<State>()
    val searchState: LiveData<State> get() = this._searchState

    fun getDataFromPref(): List<Track> {
        val result = searchHistoryManager.getSearchHistory()
        if (result.isEmpty()) {
            this._searchState.value = State.EmptyHistory
        } else {
            this._searchState.value = State.LoadedHistory
        }
        this._historyTrack.value = result
        return result
    }

    fun getDataFromServer(query: String) {
        if (query.isEmpty() or query.isBlank()) {
            return
        } else {
            this._searchState.postValue(State.Loading)

            viewModelScope.launch {
                searchTrackUseCase
                    .searchTracks(query)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundNames: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundNames != null) {
            tracks.addAll(foundNames)
        }
        when {
            errorMessage != null -> {
                this@SearchViewModel._searchState.postValue(State.Error)
            }

            tracks.isEmpty() -> {
                this@SearchViewModel._searchState.postValue(State.EmptyMainSearch)
            }

            else -> {
                this@SearchViewModel._searchState.postValue(State.SuccessSearch(tracks))
            }
        }
    }

    fun clearedList() {
        searchHistoryManager.clearHistory()
        this._searchState.value = State.ClearSearchNoQuery
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
        data object ClearSearch : State()
        data object ClearEditText : State()
        data object ClearSearchNoQuery : State()
        data class SuccessSearch(val listTracks: List<Track>) : State()
    }
}