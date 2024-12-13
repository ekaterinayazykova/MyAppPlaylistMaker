package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.Track

interface MediaPlayerInteractor {
    fun play()
    fun execute(track: Track)
    fun pause()
    fun stop()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}