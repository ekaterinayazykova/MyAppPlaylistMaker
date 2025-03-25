package com.example.myappplaylistmaker.data.db.impl

import android.util.Log
import androidx.room.Query
import androidx.room.Transaction
import com.example.myappplaylistmaker.data.converter.PlaylistDbConverter
import com.example.myappplaylistmaker.data.converter.TrackDbConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.db.cross_ref.PlaylistWithTracks
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import com.example.myappplaylistmaker.domain.db.PlaylistRepository
import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapToEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int, trackIds: List<String>) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
        appDatabase.playlistTrackCrossRefDao().deleteAllCrossRefsForPlaylist(playlistId)

        trackIds.forEach{ trackId ->
            val isInFavorites = appDatabase.trackDao().isTrackInFavorites(trackId)
            val usageCount = appDatabase.playlistTrackCrossRefDao().getTrackUsageCount(trackId)
            if (!isInFavorites && usageCount == 0) {
                appDatabase.trackDao().deleteTrackById(trackId)
            }
        }
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

    override fun getPlaylistWithTracks(): Flow<List<DomainPlaylistWithTracks>> {
        return appDatabase.playlistDao().getPlaylistWithTracks()
            .map { listOfPlaylistWithTracksEntities ->
                listOfPlaylistWithTracksEntities.map { entity ->
                    DomainPlaylistWithTracks(
                        playlist = playlistDbConverter.mapToPlaylist(entity.playlist),
                        tracks = convertFromTrackEntities(entity.tracks)
                    )
                }
            }
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        Log.d("TAG", "getPlaylistById called with playlistId: $playlistId") // Логируем вызов метода с ID плейлиста

        return appDatabase.playlistDao().getPlaylistById(playlistId)
            .map { playlistEntity ->
                Log.d("TAG", "Fetched playlistEntity from DB: $playlistEntity") // Логируем полученные данные из базы
                val playlist = playlistDbConverter.mapToPlaylist(playlistEntity) // Преобразуем в объект Playlist
                Log.d("TAG", "Converted playlistEntity to Playlist: $playlist") // Логируем результат преобразования
                playlist
            }
    }

    override fun updatePlaylistInfo(playlistId: Int){

    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.mapToEntity(playlist)
        val updatedRows = appDatabase.playlistDao().updatePlaylist(playlistEntity)
        Log.d("TAG", "Number of rows updated: $updatedRows")
    }

    private fun convertFromPlaylistEntity(flowEntities: Flow<List<PlaylistEntity>>): Flow<List<Playlist>> {
        return flowEntities.map { listOfEntities ->
            listOfEntities.map { entity -> playlistDbConverter.mapToPlaylist(entity) }
        }
    }
    private fun convertFromTrackEntities(trackEntities: List<TrackEntity>): List<Track> {
        return trackEntities.map { trackEntity ->
            trackDbConverter.mapToTrack(trackEntity)
        }
    }
}
