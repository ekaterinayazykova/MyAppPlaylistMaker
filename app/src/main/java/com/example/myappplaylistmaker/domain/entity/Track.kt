package com.example.myappplaylistmaker.domain.entity

import java.io.Serializable
import java.sql.Timestamp
import java.util.Date

data class Track (
        val trackId: String,
        val trackName: String? = null,
        val artistName: String? = null,
        val artworkUrl100: String,
        val trackTimeMillis: Long =0L,
        val collectionName: String? = null,
        val releaseDate: String? = null,
        val genre: String? = null,
        val country: String? = null,
        val previewUrl: String,
        val timestamp: Long? = null,
        var isFavorite: Boolean = false

    ) : Serializable {
        fun getAlbumCover() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

}