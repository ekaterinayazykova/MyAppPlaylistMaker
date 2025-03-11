package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.Track

interface TracksToPlaylistInteractor {

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)
    suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int)

}