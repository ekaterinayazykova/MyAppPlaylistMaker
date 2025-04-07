package com.example.myappplaylistmaker.data.converter

import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.domain.entity.Playlist

class PlaylistDbConverter {

    fun mapToEntity(playlist: Playlist): PlaylistEntity{
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            amountOfTracks = playlist.amountOfTracks,
            timestamp = System.currentTimeMillis()
        )
    }

    fun mapToPlaylist(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            amountOfTracks = playlist.amountOfTracks,
            timestamp = playlist.timestamp
        )
    }
}