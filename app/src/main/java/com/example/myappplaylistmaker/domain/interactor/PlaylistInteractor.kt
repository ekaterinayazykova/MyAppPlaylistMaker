package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int, trackIds: List<String>)
    fun getPlaylist(): Flow<List<Playlist>>
    fun getPlaylistWithTracks(): Flow<List<DomainPlaylistWithTracks>>
    fun getPlaylistById(playlistId: Int): Flow<Playlist>
    suspend fun updatePlaylist(playlist: Playlist)
}