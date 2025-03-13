package com.example.myappplaylistmaker.domain.impl

import android.util.Log
import com.example.myappplaylistmaker.core.PlayerControl
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerInteractorImpl (private val mediaPlayerRepository: MediaPlayerRepository) : MediaPlayerInteractor {

    private val playerControl = PlayerControl()
    private var onCompletionListener: (() -> Unit)? = null

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
        mediaPlayerRepository.setOnCompletionListener {
            onCompletionListener?.invoke()
        }
    }

    override fun play() {
        mediaPlayerRepository.play()
        playerControl.play()
    }

    override fun execute(track: Track, playerPrepared: () -> Unit)  {
        mediaPlayerRepository.preparePlayer(track.previewUrl ?: "", playerPrepared)
//        mediaPlayerRepository.play()
//        playerControl.play()
    }

    override fun pause() {
        mediaPlayerRepository.pause()
        playerControl.pause()
    }

    override fun stop() {
        mediaPlayerRepository.release()
        playerControl.stop()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return playerControl.isPlaying()
    }
}