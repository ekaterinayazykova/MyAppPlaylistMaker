package com.example.myappplaylistmaker.data.db.impl

import android.util.Log
import com.example.myappplaylistmaker.data.converter.TrackDbConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.myappplaylistmaker.domain.db.TracksToPlaylistRepository
import com.example.myappplaylistmaker.domain.entity.Track

class TracksToPlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter): TracksToPlaylistRepository {

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Int) {
        val before = appDatabase.playlistTrackCrossRefDao().getAllCrossRefs()
        Log.d("DEBUG_DB", "Before insert: $before")
        val trackEntity = trackDbConverter.mapToEntity(track)
        appDatabase.trackDao().insertTrackToFavs(trackEntity)
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = track.trackId,
            addedAt = System.currentTimeMillis()
        )
        appDatabase.playlistTrackCrossRefDao().insertCrossRef(crossRef)
        val after = appDatabase.playlistTrackCrossRefDao().getAllCrossRefs()
        Log.d("DEBUG_DB", "After insert: $after")
        appDatabase.playlistDao().updatePlaylist(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = trackId,
            addedAt = System.currentTimeMillis()
        )
        appDatabase.playlistTrackCrossRefDao().deleteCrossRef(crossRef)

        val isInFavorites = appDatabase.trackDao().isTrackInFavorites(trackId)
        val usageCount = appDatabase.playlistTrackCrossRefDao().getTrackUsageCount(trackId)
        Log.d("TAG", "isInFavorites = $isInFavorites, usageCount = $usageCount")
        if (!isInFavorites && usageCount == 0) {
            Log.d("TAG", "delete method called")
            appDatabase.trackDao().deleteTrackById(trackId)
        }
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean {
        return appDatabase.playlistTrackCrossRefDao().isTrackInPlaylist(playlistId, trackId)
    }
}