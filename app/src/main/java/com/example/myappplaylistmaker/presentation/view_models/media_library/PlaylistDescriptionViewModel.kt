package com.example.myappplaylistmaker.presentation.view_models.media_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import com.example.myappplaylistmaker.domain.interactor.TracksToPlaylistInteractor
import com.example.myappplaylistmaker.domain.use_case.SharePlaylistUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistDescriptionViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val tracksToPlaylistInteractor: TracksToPlaylistInteractor,
    private val sharePlaylistUseCase: SharePlaylistUseCase
) : ViewModel() {

    private val _contentState = MutableLiveData<PlaylistContent>()
    val contentState: LiveData<PlaylistContent> get() = this._contentState

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistWithTracks()
                .map { listOfPlaylistWithTracks ->
                    listOfPlaylistWithTracks.firstOrNull { it.playlist.playlistId == playlistId }
                }
                .collect { domainPlaylistWithTracks ->
                    domainPlaylistWithTracks?.let {
                        if (it.tracks.isEmpty()) {
                            _contentState.value = PlaylistContent.NoContent(it)
                        } else {
                            _contentState.value = PlaylistContent.Content(it)
                        }
                    }

                }
        }
    }

    fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        viewModelScope.launch {
            tracksToPlaylistInteractor.removeTrackFromPlaylist(trackId, playlistId)
        }
    }

    fun deletePlaylist(playlistId: Int, trackIds: List<String>) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId, trackIds)
        }
    }

    fun sharePlaylist(shareMessage: String) {
        sharePlaylistUseCase.sharePlaylist(shareMessage)
    }

    sealed class PlaylistContent {
        data class Content(val listTracks: DomainPlaylistWithTracks) : PlaylistContent()
        data class NoContent(val playlistInfo: DomainPlaylistWithTracks) : PlaylistContent()
    }
}