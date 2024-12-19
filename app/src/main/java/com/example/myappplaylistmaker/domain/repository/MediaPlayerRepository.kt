package com.example.myappplaylistmaker.domain.repository

interface MediaPlayerRepository {
    fun praparePlayer(songUrl: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}