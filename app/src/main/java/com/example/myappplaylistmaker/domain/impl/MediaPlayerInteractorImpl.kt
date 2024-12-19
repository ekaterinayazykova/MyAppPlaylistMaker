package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.entity.PlayerControl
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerInteractorImpl (private val mediaPlayerRepository: MediaPlayerRepository) : MediaPlayerInteractor {

    private val playerControl = PlayerControl()

    override fun play() {
        mediaPlayerRepository.play()
        playerControl.play()
    }

    override fun execute(track: Track) {
        mediaPlayerRepository.praparePlayer(track.previewUrl)
        mediaPlayerRepository.play()
        playerControl.play()
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