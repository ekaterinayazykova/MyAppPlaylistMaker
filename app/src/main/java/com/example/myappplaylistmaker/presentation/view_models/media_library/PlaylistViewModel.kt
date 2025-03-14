package com.example.myappplaylistmaker.presentation.view_models.media_library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val _playlistState = MutableLiveData<List<Playlist>>()
    val playlistState: LiveData<List<Playlist>> get() = this._playlistState

    fun getPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylist()
                .collect { playlists ->
                    Log.d("PLAY", "Загружено плейлистов: ${playlists.size}")
                    _playlistState.value = playlists
                }
        }
    }
}
