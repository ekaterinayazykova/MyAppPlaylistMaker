package com.example.myappplaylistmaker.presentation.view_models.media_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.view_models.search.SearchViewModel.State

class FavTracksViewModel() : ViewModel() {

    private val _historyTrack: MutableLiveData<List<Track>?> = MutableLiveData()
    val historyTrack: LiveData<List<Track>?> get() = this._historyTrack

    private val _searchState = MutableLiveData<State>()
    val searchState: LiveData<State> get() = this._searchState

    fun getDataFromPref() {
        // можно сделать получение как с треками из истории
        // обзервить то что кладется в переменную history tracks,
        // в зависимости от результата устанавливать статус
        // сделать метод с обзервом статуса и менять представление на экране
    }

    sealed class State {
        data object EmptyFavTracks : State()
        data object LoadedFavTracks : State()
        data object Loading : State()
    }
}