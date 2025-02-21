package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {

    suspend fun addTrackToFavs(track: Track)
    suspend fun removeTrackFromFavs(track: Track)
    fun getFavsTracks(): Flow<List<Track>>
}