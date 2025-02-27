package com.example.myappplaylistmaker.data.model

import java.io.Serializable

data class TrackDto (
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val trackTimeMillis: Long,
    val collectionName: String,
    val releaseDate: String,
    val genre: String? = null,
    val country: String,
    val previewUrl: String

) : Serializable {
    fun getAlbumCover() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}