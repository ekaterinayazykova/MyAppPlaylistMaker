package com.example.myappplaylistmaker.data.converter

import com.example.myappplaylistmaker.data.model.TrackDto
import com.example.myappplaylistmaker.domain.entity.Track

object TrackConverter {
    fun map(trackDto: TrackDto) : Track {
            val trackName = trackDto.trackName ?: "Неизвестный трек"
            val artistName = trackDto.artistName ?: "Неизвестный артист"
            val artworkUrl100 = trackDto.artworkUrl100 ?: ""
            val trackTimeMillis = trackDto.trackTimeMillis ?: 0L
            val collectionName = trackDto.collectionName ?: ""
            val releaseDate = trackDto.releaseDate ?: ""
            val country = trackDto.country ?: ""
            val previewUrl = trackDto.previewUrl ?: ""

        return Track(
            trackName = trackName,
            artistName = artistName,
            artworkUrl100 = artworkUrl100,
            trackTimeMillis = trackTimeMillis,
            collectionName = collectionName,
            releaseDate = releaseDate,
            country = country,
            previewUrl = previewUrl
        )

    }
}