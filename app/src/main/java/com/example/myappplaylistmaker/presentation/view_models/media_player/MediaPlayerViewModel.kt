package com.example.myappplaylistmaker.presentation.view_models.media_player

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.domain.entity.PlayerState
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor

class MediaPlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    init {
        mediaPlayerInteractor.setOnCompletionListener {
            onTrackComplete()
        }
    }

    private var playerState = PlayerState.DEFAULT
    private var handler: Handler? = null

    private fun onTrackComplete() {
        handler?.removeCallbacksAndMessages(null)
        playerState = PlayerState.PREPARED
        _state.postValue(State.PAUSED("00:00"))
//        _state.postValue(State.PREPARED)
    }

    fun setTrack(track: Track) {
        preparePlayer(track)
    }

    fun preparePlayer(track: Track) {
        val songUrl: String = track.previewUrl ?: ""
        if (songUrl.isNotEmpty() && playerState != PlayerState.PREPARED) {
            _state.postValue(State.LOADING)
            mediaPlayerInteractor.execute(track) {
                playerState = PlayerState.PREPARED
                _state.postValue(State.PREPARED)
            }
        } else {
            playerState = PlayerState.PREPARED
        }
    }

    fun startPlayer() {
        mediaPlayerInteractor.play()
        val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
        val minutes = (currentPositionMillis / 1000) / 60
        val seconds = (currentPositionMillis / 1000) % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        startCountdown()
        playerState = PlayerState.PLAYING
        _state.postValue(State.PLAYING(formattedTime))
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pause()

        if (playerState == PlayerState.PLAYING) {
            val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
            val minutes = (currentPositionMillis / 1000) / 60
            val seconds = (currentPositionMillis / 1000) % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            playerState = PlayerState.PAUSED
            _state.postValue(State.PAUSED(formattedTime))
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
        _state.postValue(State.STOPPED)
    }

    fun cleanup() {
        handler?.removeCallbacksAndMessages(null)
    }


    private fun startCountdown() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayerInteractor.isPlaying()) {
                    val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    if (playerState == PlayerState.PLAYING) {
                        _state.postValue(State.PLAYING(formattedTime))
                    }
                    handler?.postDelayed(this, 1000)

                } else {
                    handler?.removeCallbacks(this)
                }
            }
        })
    }

    sealed class State {
        data object LOADING: State()
        data object PREPARED: State()
        data class PLAYING(val currentTime: String) : State()
        data class PAUSED(val currentTime: String) : State()
        data object STOPPED: State()
    }
}