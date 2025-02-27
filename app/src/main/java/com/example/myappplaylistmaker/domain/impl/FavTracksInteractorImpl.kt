package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.db.FavTracksRepository
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavTracksInteractorImpl(private val favTracksRepository: FavTracksRepository): FavTracksInteractor {
    override suspend fun addTrackToFavs(track: Track) {
        favTracksRepository.addTrackToFavs(track)
    }

    override suspend fun removeTrackFromFavs(track: Track) {
        favTracksRepository.removeTrackFromFavs(track)
    }

    override suspend fun getFavTrackIds(): List<String> {
        return favTracksRepository.getFavTrackId()
    }

    override fun getFavsTracks(): Flow<List<Track>> {
        return favTracksRepository.getFavsTracks()
    }
}