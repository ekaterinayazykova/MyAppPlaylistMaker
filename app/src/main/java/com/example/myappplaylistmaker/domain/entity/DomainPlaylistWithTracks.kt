package com.example.myappplaylistmaker.domain.entity

data class DomainPlaylistWithTracks(
    val playlist: Playlist,
    val tracks: List<Track>
)
