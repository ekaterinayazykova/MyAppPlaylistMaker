package com.example.myappplaylistmaker.domain.db

import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksRepository {

    suspend fun addTrackToFavs(track: Track)
    suspend fun removeTrackFromFavs(track: Track)
    fun getFavsTracks(): Flow<List<Track>>
    suspend fun getFavTrackId(): List<String>

}