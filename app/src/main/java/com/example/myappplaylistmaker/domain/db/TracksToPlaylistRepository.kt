package com.example.myappplaylistmaker.domain.db

import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TracksToPlaylistRepository {

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
    suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int)
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean

}