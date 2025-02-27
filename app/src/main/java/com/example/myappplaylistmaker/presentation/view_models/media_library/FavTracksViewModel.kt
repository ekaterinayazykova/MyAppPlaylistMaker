package com.example.myappplaylistmaker.presentation.view_models.media_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import kotlinx.coroutines.launch

class FavTracksViewModel(
    private val favTracksInteractor: FavTracksInteractor) : ViewModel() {

    private val _favTracks = MutableLiveData<List<Track>>()
    val favTracks: LiveData<List<Track>> get() = this._favTracks

    fun getFavTracks() {
        viewModelScope.launch {
            favTracksInteractor.getFavsTracks().collect { tracks ->
                _favTracks.value = tracks
            }
        }
    }
}