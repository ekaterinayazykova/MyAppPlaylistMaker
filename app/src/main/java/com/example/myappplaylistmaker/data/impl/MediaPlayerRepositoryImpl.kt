package com.example.myappplaylistmaker.data.impl

import android.media.MediaPlayer
import android.util.Log
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl (private var mediaPlayer: MediaPlayer?) : MediaPlayerRepository {

//    private var mediaPlayer: MediaPlayer? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun preparePlayer(songUrl: String, playerPrepared: () -> Unit) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songUrl)
            prepareAsync()
            setOnPreparedListener {
                playerPrepared.invoke()
            }
            setOnCompletionListener {
                onCompletionListener?.invoke()
            }
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}