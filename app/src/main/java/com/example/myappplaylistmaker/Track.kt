package com.example.myappplaylistmaker

class Track (
        val trackName: String,
        val artistName: String,
        val trackTimeMillis: Long,
        val artworkUrl100: String
    ) {
    fun getTrackDuration(): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:@02d", minutes, seconds)
    }
}