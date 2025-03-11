package com.example.myappplaylistmaker.data.db.impl

import com.example.myappplaylistmaker.data.converter.PlaylistDbConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.domain.db.PlaylistRepository
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapToEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapToEntity(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override fun getPlaylist(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylistWithTracks()
            .map { listOfPlaylistWithTracks ->
                listOfPlaylistWithTracks.map { playlistWithTracks ->
                    playlistDbConverter.mapToPlaylist(playlistWithTracks.playlist)
                        .copy(amountOfTracks = playlistWithTracks.tracks.size)
                }
            }
    }

//    override fun getPlaylistsWithTracksCount(): Flow<List<Playlist>> {
//        return appDatabase.playlistDao().getPlaylistsWithTracksCount()
//            .map { listOfPlaylistWithTracks ->
//                listOfPlaylistWithTracks.map { playlistWithTracks ->
//                    playlistDbConverter.mapToPlaylist(playlistWithTracks.playlist)
//                        .copy(amountOfTracks = playlistWithTracks.tracks.size)
//                }
//            }
//    }

    private fun convertFromPlaylistEntity(flowEntities: Flow<List<PlaylistEntity>>): Flow<List<Playlist>> {
        return flowEntities.map { listOfEntities ->
            listOfEntities.map { entity -> playlistDbConverter.mapToPlaylist(entity) }
        }
    }
}