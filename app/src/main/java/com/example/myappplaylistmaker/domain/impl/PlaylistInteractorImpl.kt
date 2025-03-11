package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.db.PlaylistRepository
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylist()
    }

//    override fun getPlaylistsWithTracksCount(): Flow<List<Playlist>> {
//        return playlistRepository.getPlaylistsWithTracksCount()
//    }
}