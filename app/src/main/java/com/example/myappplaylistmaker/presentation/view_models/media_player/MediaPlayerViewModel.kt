package com.example.myappplaylistmaker.presentation.view_models.media_player

import android.annotation.SuppressLint
import android.graphics.Path.Op
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.entity.PlayerState
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor

class MediaPlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    private val playerOption = MutableLiveData<Option?>()
    val choosePlayerOption: LiveData<Option?> get() = playerOption

    private val _durationTime = MutableLiveData<String>()
    val durationTime: LiveData<String> get() = _durationTime

    private var playerState = PlayerState.DEFAULT
    private var handler: Handler? = null

    fun play() {
        playerOption.value = Option.PLAY
    }

    fun prepare(track: Track) {
        playerOption.value = Option.PREPARE
    }

    fun pause() {
        playerOption.value = Option.PAUSE
    }

    fun playback() {
        playerOption.value = Option.PLAYBACK
    }

    fun preparePlayer(track: Track) {
        var songUrl: String = track.previewUrl
        if (songUrl.isNotEmpty()) {
            mediaPlayerInteractor.execute(track)
            playerState = PlayerState.PREPARED
        }
    }

    fun startPlayer() {
        mediaPlayerInteractor.play()
        startCountdown()
        playerState = PlayerState.PLAYING
    }
    fun isPlaying(): Boolean {
        return playerState == PlayerState.PLAYING
    }

    fun pausePlayer() {
        if (mediaPlayerInteractor.isPlaying()) {
            mediaPlayerInteractor.pause()
            playerState = PlayerState.PAUSED
        }
    }

    fun playbackControl() {
        Log.e("PlayerState", "${playerState}")
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {
                Log.e("PlaybackControl", "PlayerErrorState")
            }
        }
    }
    fun startCountdown() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayerInteractor.isPlaying()) {
                    val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    _durationTime.postValue(formattedTime)
                    handler?.postDelayed(this, 1000)

                } else {
                    mediaPlayerInteractor.pause()
                    handler?.removeCallbacks(this)
                }
            }
        })
    }

    sealed class Option {
        object PLAY : Option()
        object PREPARE : Option()
        object PAUSE : Option()
        object PLAYBACK : Option()
    }
}