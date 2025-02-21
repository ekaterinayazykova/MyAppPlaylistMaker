package com.example.myappplaylistmaker.data.db.impl

import com.example.myappplaylistmaker.data.converter.TrackDbConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import com.example.myappplaylistmaker.domain.db.FavTracksRepository
import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTracksRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavTracksRepository {
    override suspend fun addTrackToFavs(track: Track) {
        val trackEntity = trackDbConverter.mapToEntity(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavs(track: Track) {
        val trackEntity = trackDbConverter.mapToEntity(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getFavsTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { tracks -> trackDbConverter.mapToTrack(tracks)}
    }
}