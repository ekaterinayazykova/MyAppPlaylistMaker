package com.example.myappplaylistmaker.presentation.view_models.media_library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import kotlinx.coroutines.launch

class FavTracksViewModel(
    private val favTracksInteractor: FavTracksInteractor) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = this._state


    fun getFavTracks() {
        viewModelScope.launch {
            favTracksInteractor.getFavsTracks().collect { favTracks ->
                Log.d("FavTracksViewModel", "collect: $favTracks")
                if (favTracks.isEmpty()) {
                    setState(State.EmptyFavTracks)
                } else {
                    setState(State.LoadedFavTracks(favTracks))
                }
            }
        }
    }

    fun setState(state: State) {
        _state.value = state
    }

    sealed class State {
        data object EmptyFavTracks : State()
        data class LoadedFavTracks(val favTracks: List<Track>) : State()
    }
}