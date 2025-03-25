package com.example.myappplaylistmaker.presentation.view_models.edit_playlist

import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import com.example.myappplaylistmaker.presentation.view_models.create_playlist.CreatePlaylistViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor):
    CreatePlaylistViewModel(playlistInteractor) {

    private val _playlistData = MutableStateFlow<Playlist?>(null)
    val playlistData: StateFlow<Playlist?> get() = _playlistData

    fun loadPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                _playlistData.value = playlist
            }
        }
    }

    fun updatePlaylistInfo(id: Int, name: String, description: String, imagePath: String) {
        viewModelScope.launch {
            val updatedPlaylist = Playlist(
                playlistId = id,
                playlistName = name,
                playlistDescription = description,
                imagePath = imagePath.ifEmpty {
                    playlistData.value?.imagePath.orEmpty()
                }
            )
            playlistInteractor.updatePlaylist(updatedPlaylist)
        }
    }
}