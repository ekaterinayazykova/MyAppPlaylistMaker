package com.example.myappplaylistmaker.domain.db

import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TracksToPlaylistRepository {

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
    suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int)

}