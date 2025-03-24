package com.example.myappplaylistmaker.presentation.view_models.edit_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import com.example.myappplaylistmaker.presentation.view_models.create_playlist.CreatePlaylistViewModel
import kotlinx.coroutines.flow.Flow

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor):
    CreatePlaylistViewModel(playlistInteractor) {

    private val _playlistId = MutableLiveData<Int>()
    val playlistId: LiveData<Int> get() = _playlistId

    private val _playlistData = MutableLiveData<Flow<Playlist>>()
    val playlistData: LiveData<Flow<Playlist>> get() = _playlistData

    init {
        _playlistId.observeForever { id ->
            loadPlaylistById(id)
        }
    }

    fun setPlaylistId(id: Int) {
        _playlistId.value = id
    }

    fun loadPlaylistById(id: Int) {
        val playlistDataFromDb = playlistInteractor.getPlaylistById(id)
        _playlistData.value = playlistDataFromDb
    }
}