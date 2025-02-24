package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.db.FavTracksRepository
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryInteractorImpl(private val historyRepository: FavTracksRepository): FavTracksInteractor {
    override suspend fun addTrackToFavs(track: Track) {
        historyRepository.addTrackToFavs(track)
    }

    override suspend fun removeTrackFromFavs(track: Track) {
        historyRepository.removeTrackFromFavs(track)
    }

    override fun getFavsTracks(): Flow<List<Track>> {
        return historyRepository.getFavsTracks()
            .map { tracks -> tracks.sortedByDescending { it.timestamp}}
    }
}