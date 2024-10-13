package com.example.myappplaylistmaker

sealed class TrackItem {
    data class SearchTrack(val track: Track) : TrackItem()
    data class HistoryTrack(val track: Track) : TrackItem()
}

class Track (
        val trackName: String,
        val artistName: String,
        val trackTimeMillis: Long,
        val artworkUrl100: String,
        val id: Any
    ) {

    fun getTrackDuration(): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}