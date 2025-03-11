package com.example.myappplaylistmaker.presentation.view_models.media_player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplaylistmaker.domain.entity.PlayerState
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import com.example.myappplaylistmaker.domain.interactor.TracksToPlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favTracksInteractor: FavTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val tracksToPlaylistInteractor: TracksToPlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> get() = _currentTrack

    private val _playlistState = MutableLiveData<List<Playlist>>()
    val playlistState: LiveData<List<Playlist>> get() = this._playlistState

    private var playerState = PlayerState.DEFAULT

    init {
        mediaPlayerInteractor.setOnCompletionListener {
            onTrackComplete()
        }
    }

    private fun onTrackComplete() {
        timerJob?.cancel()
        playerState = PlayerState.PREPARED
        _state.postValue(State.PAUSED("00:00"))
    }

    fun setTrack(track: Track) {
        Log.d("MediaPlayerVM", "setTrack() => LOADING")
//        _state.value = State.LOADING
        _currentTrack.value = track
        preparePlayer(track)
    }

    fun onFavoriteClicked() {
        val track = _currentTrack.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTrack = track.copy(isFavorite = !track.isFavorite)
            if (updatedTrack.isFavorite) {
                favTracksInteractor.addTrackToFavs(updatedTrack)
            } else {
                favTracksInteractor.removeTrackFromFavs(updatedTrack)
            }
            withContext(Dispatchers.Main) {
                _currentTrack.value = updatedTrack
            }
        }
    }

    fun checkFavorite(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            val favTrackId = favTracksInteractor.getFavTrackIds()
            val updatedTracks = track.copy(isFavorite = track.trackId in favTrackId)
            withContext(Dispatchers.Main) {
                _currentTrack.value = updatedTracks
            }
        }
    }

    fun preparePlayer(track: Track) {
        val songUrl: String = track.previewUrl ?: ""
        if (songUrl.isNotEmpty() && playerState != PlayerState.PREPARED) {
            Log.d("MediaPlayerVM", "preparePlayer() => LOADING before async")
            _state.postValue(State.LOADING)
            mediaPlayerInteractor.execute(track) {
                Log.d("MediaPlayerVM", "onPrepared => PREPARED")
                playerState = PlayerState.PREPARED
                _state.postValue(State.PREPARED)
            }
        } else {
            playerState = PlayerState.PREPARED
        }
    }

    fun startPlayer() {
        if (playerState == PlayerState.PREPARED || playerState == PlayerState.PAUSED) {
            mediaPlayerInteractor.play()
            playerState = PlayerState.PLAYING
            _state.postValue(State.PLAYING("00:00"))
            startTimer()
        }
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pause()

        if (playerState == PlayerState.PLAYING) {
            timerJob?.cancel()
            playerState = PlayerState.PAUSED
            _state.postValue(State.PAUSED(getCurrentPlayerPosition()))
        }
    }

    fun isPlaying(): Boolean {
        return playerState == PlayerState.PLAYING
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {
                Log.e("ErrorState", "PlayerErrorState")
            }
        }
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stop()
        playerState = PlayerState.DEFAULT
        _state.postValue(State.PAUSED(getCurrentPlayerPosition()))
    }

    fun cleanup() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.isPlaying()) {
                delay(300L)
                _state.postValue(State.PLAYING(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayerInteractor.getCurrentPosition()) ?: "00:00"
    }

    fun getPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylist()
                .collect { playlists ->
                    _playlistState.value = playlists
                }
        }
    }

    fun addTrackToPlaylist(track: Track, playlistId: Int, ) {
        viewModelScope.launch {
            tracksToPlaylistInteractor.addTrackToPlaylist(
                track = track,
                playlistId = playlistId
            )
        }
    }

    sealed class State(val progress: String) {
        data object LOADING : State("00:00")
        data object PREPARED : State("00:00")
        data class PLAYING(val currentTime: String) : State(progress = currentTime)
        data class PAUSED(val currentTime: String) : State(progress = currentTime)
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}