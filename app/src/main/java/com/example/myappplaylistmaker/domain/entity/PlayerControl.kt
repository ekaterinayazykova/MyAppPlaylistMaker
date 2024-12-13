package com.example.myappplaylistmaker.domain.entity

class PlayerControl  (
    var currentState: PlayerState = PlayerState.PREPARED
) {
    fun play() {
        currentState = PlayerState.PLAYING
    }
    fun pause() {
        currentState = PlayerState.PAUSED
    }
    fun stop() {
        currentState = PlayerState.PREPARED
    }
    fun isPlaying(): Boolean {
        return currentState == PlayerState.PLAYING
    }

}