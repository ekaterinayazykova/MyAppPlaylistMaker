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

    fun updatePlayerOption(state: Option) {
        playerOption.value = state
    }

    fun stop() {
        mediaPlayerInteractor.stop()
    }

    fun preparePlayer(track: Track) {
        val songUrl: String = track.previewUrl ?: ""
        if (songUrl.isNotEmpty() && playerState != PlayerState.PREPARED) {
            updatePlayerOption(Option.LOADING)
            mediaPlayerInteractor.execute(track) {
                playerState = PlayerState.PREPARED
                updatePlayerOption(Option.PREPARE)
            }
        } else {
            playerState = PlayerState.PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayerInteractor.play()
        startCountdown()
        playerState = PlayerState.PLAYING
        updatePlayerOption(Option.PLAYING)
    }
    fun isPlaying(): Boolean {
        return playerState == PlayerState.PLAYING
    }

    fun pausePlayer() {
        if (isPlaying()) {
            mediaPlayerInteractor.pause()
            playerState = PlayerState.PAUSED
            updatePlayerOption(Option.PAUSE)
        }
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> updatePlayerOption(Option.PREPARE)
        }
    }
    private fun startCountdown() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (isPlaying()) {
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
        data object LOADING : Option()
        data object PREPARE : Option()
        data object PLAYBACK : Option()
        data object PLAYING : Option()
        data object PAUSE : Option()
    }
}