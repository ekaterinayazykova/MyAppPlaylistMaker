package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.Track

interface TracksToPlaylistInteractor {

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
    suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int)
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean

}