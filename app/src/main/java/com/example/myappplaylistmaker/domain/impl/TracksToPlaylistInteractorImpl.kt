package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.db.TracksToPlaylistRepository
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.TracksToPlaylistInteractor

class TracksToPlaylistInteractorImpl(private val tracksToPlaylistRepository: TracksToPlaylistRepository) :
    TracksToPlaylistInteractor {
    override suspend fun addTrackToPlaylist(track: Track, playlistId: Int) {
        tracksToPlaylistRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        tracksToPlaylistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean {
        return tracksToPlaylistRepository.isTrackInPlaylist(playlistId, trackId)
    }
}