package com.example.myappplaylistmaker.domain.repository

interface SharePlaylistRepository {
    fun sharePlaylist(shareMessage: String)
}