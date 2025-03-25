package com.example.myappplaylistmaker.presentation.view_models.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryManager: SearchHistoryManagerInteractor,
    private val searchTrackUseCase: SearchTrackUseCase,
    private val stringProvider: StringProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val LAST_QUERY_KEY = "last_query"
        private const val SEARCH_RESULTS_KEY = "search_results"
    }

    private val _searchState = MutableLiveData<State>()
    val searchState: LiveData<State> get() = this._searchState

    var lastQuery: String?
        get() = savedStateHandle.get(LAST_QUERY_KEY)
        set(value) {
            savedStateHandle[LAST_QUERY_KEY] = value
        }

    fun getDataFromPref() {
        viewModelScope.launch {
            val result = searchHistoryManager.getSearchHistory()

            if (result.isEmpty()) {
                setState(State.EmptyHistory)
            } else {
                setState(State.LoadedHistory(result))
            }
        }
    }

    fun getDataFromServer(query: String) {
        Log.d("SearchViewModel", "Вход в getDataFromServer с запросом: '$query'")
        if (query.isEmpty() || query.isBlank()) return

        if (query == lastQuery) return
        lastQuery = query
        _searchState.postValue(State.Loading)

        viewModelScope.launch {
            Log.d("SearchViewModel", "Запрос '$query' совпадает с lastQuery, запрос не отправляется")

            searchTrackUseCase.searchTracks(query)
                .catch { e ->
                    Log.e("SearchViewModel", "Ошибка при поиске: ${e.message}", e)
                    _searchState.postValue(State.Error)
                }
                .collect { pair ->
                    Log.d("SearchViewModel", "Результаты запроса: ${pair.first}, ${pair.second}")
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                Log.d("SearchViewModel", "Ошибка запроса: $errorMessage")
                _searchState.postValue(State.Error)
            }
            foundTracks.isNullOrEmpty() -> {
                Log.d("SearchViewModel", "Пустой результат запроса")
                _searchState.postValue(State.EmptyMainSearch)
            }
            else -> {
                Log.d("SearchViewModel", "Успешный запрос, найдено ${foundTracks.size} треков")

//                cachedResults = foundTracks
                _searchState.postValue(State.SuccessSearch(foundTracks))
            }
        }
    }

    fun setState(state: State) {
        _searchState.value = state
    }

    fun clearedList() {
        searchHistoryManager.clearHistory()
        this.setState(State.ClearSearchNoQuery)
    }

    fun saveToHistory(track: Track) {
        searchHistoryManager.saveToHistory(track)
    }

    fun clearPrefs() {
        searchHistoryManager.clearPrefs()
    }

    sealed class State {
        data object EmptyHistory : State()
        data class LoadedHistory(val listTracks: List<Track>) : State()
        data object Error : State()
        data object Loading : State()
        data object EmptyMainSearch : State()
        data object ClearSearch : State()
        data object ClearEditText : State()
        data object ClearSearchNoQuery : State()
        data class SuccessSearch(val listTracks: List<Track>) : State()
    }
}