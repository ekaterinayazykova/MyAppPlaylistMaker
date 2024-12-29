package com.example.myappplaylistmaker.domain.repository

interface MediaPlayerRepository {
    fun praparePlayer(songUrl: String, playerPrepared: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}