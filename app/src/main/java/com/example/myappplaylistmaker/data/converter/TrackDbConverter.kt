package com.example.myappplaylistmaker.data.converter

import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import com.example.myappplaylistmaker.domain.entity.Track

class TrackDbConverter {

    fun mapToEntity(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            artworkUrl = track.artworkUrl100,
            trackTimeMillis = track.trackTimeMillis,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genre = track.genre ?: "Unknown",
            country = track.country,
            previewUrl = track.previewUrl,
            timestamp = System.currentTimeMillis(),
            isFavorite = track.isFavorite
        )
    }

    fun mapToTrack(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            artworkUrl100 = track.artworkUrl,
            trackTimeMillis = track.trackTimeMillis,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genre = track.genre,
            country = track.country,
            previewUrl = track.previewUrl,
            timestamp = track.timestamp,
            isFavorite = track.isFavorite
        )
    }
}