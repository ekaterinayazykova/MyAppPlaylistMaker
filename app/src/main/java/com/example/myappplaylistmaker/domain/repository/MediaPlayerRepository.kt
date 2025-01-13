package com.example.myappplaylistmaker.domain.repository

interface MediaPlayerRepository {
    fun preparePlayer(songUrl: String, playerPrepared: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)

}