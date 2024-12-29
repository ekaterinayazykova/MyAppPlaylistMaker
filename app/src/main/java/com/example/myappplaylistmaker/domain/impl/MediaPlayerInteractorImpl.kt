package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.entity.PlayerControl
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerInteractorImpl (private val mediaPlayerRepository: MediaPlayerRepository) : MediaPlayerInteractor {

    override fun play() {
        mediaPlayerRepository.play()
    }

    override fun execute(track: Track, playerPrepared: () -> Unit) {
        mediaPlayerRepository.praparePlayer(track.previewUrl ?: "", playerPrepared)
    }

    override fun pause() {
        mediaPlayerRepository.pause()
    }

    override fun stop() {
        mediaPlayerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }
}