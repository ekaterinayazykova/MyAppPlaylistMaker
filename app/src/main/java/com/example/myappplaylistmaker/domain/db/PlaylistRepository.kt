package com.example.myappplaylistmaker.domain.db

import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int, trackIds: List<String>)
    fun getPlaylist(): Flow<List<Playlist>>
    fun getPlaylistWithTracks(): Flow<List<DomainPlaylistWithTracks>>
    fun getPlaylistById(playlistId: Int): Flow<Playlist>
    fun updatePlaylistInfo(playlistId: Int)

}
