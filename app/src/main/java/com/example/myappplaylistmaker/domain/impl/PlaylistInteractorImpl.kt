package com.example.myappplaylistmaker.domain.impl

import android.util.Log
import com.example.myappplaylistmaker.domain.db.PlaylistRepository
import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int, trackIds: List<String>) {
        playlistRepository.deletePlaylist(playlistId, trackIds)
    }

    override fun getPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylist()
    }

    override fun getPlaylistWithTracks(): Flow<List<DomainPlaylistWithTracks>> {
        return playlistRepository.getPlaylistWithTracks()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        Log.d("TAG", "Getter called")
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        Log.d("TAG", "Updating playlist in DB")
        playlistRepository.updatePlaylist(playlist)
    }
}