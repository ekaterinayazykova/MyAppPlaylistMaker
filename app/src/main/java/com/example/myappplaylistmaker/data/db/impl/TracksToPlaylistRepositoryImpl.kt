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
        appDatabase.trackDao().insertTrack(trackEntity)
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = track.trackId,
            addedAt = System.currentTimeMillis()
        )
        appDatabase.playlistTrackCrossRefDao().insertCrossRef(crossRef)

        val after = appDatabase.playlistTrackCrossRefDao().getAllCrossRefs()
        Log.d("DEBUG_DB", "After insert: $after")
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int) {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = track.trackId,
            addedAt = System.currentTimeMillis()
        )
        appDatabase.playlistTrackCrossRefDao().deleteCrossRef(crossRef)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean {
        return appDatabase.playlistTrackCrossRefDao().isTrackInPlaylist(playlistId, trackId)
    }
}