package com.example.myappplaylistmaker.domain.entity

data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String = "playlist_placeholder",
    val amountOfTracks: Int? = null,
    val timestamp: Long? = System.currentTimeMillis()
)
