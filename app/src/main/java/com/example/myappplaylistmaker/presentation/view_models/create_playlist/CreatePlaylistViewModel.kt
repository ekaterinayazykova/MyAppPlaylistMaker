package com.example.myappplaylistmaker.presentation.view_models.create_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun addPlaylist(name: String, description: String, imagePath: String) {
        viewModelScope.launch {
            val newPlaylist = Playlist(
                playlistName = name,
                playlistDescription = description,
                imagePath = imagePath,
                amountOfTracks = 0
            )
            playlistInteractor.addPlaylist(newPlaylist)
        }
    }
}