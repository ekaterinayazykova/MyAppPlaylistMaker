package com.example.myappplaylistmaker.domain.db

import com.example.myappplaylistmaker.domain.entity.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylist(): Flow<List<Playlist>>
//    fun getPlaylistsWithTracksCount(): Flow<List<Playlist>>
}
