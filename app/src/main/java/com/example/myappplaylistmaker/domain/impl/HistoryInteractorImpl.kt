package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.db.FavTracksRepository
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryInteractorImpl(private val historyRepository: FavTracksRepository): FavTracksInteractor {
    override suspend fun addTrackToFavs(track: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTrackFromFavs(track: Track) {
        TODO("Not yet implemented")
    }


    override fun getFavsTracks(): Flow<List<Track>> {
        return historyRepository.getFavsTracks()
            .map { tracks -> tracks.sortedByDescending { it.timestamp}}
    }
}